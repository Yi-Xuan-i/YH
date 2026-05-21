package com.yixuan.yh.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.common.mybatis.BaseServiceImpl;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.product.constant.RabbitMQConstant;
import com.yixuan.yh.product.constant.RedisConstant;
import com.yixuan.yh.product.constant.RedisLuaResultConstant;
import com.yixuan.yh.product.mapper.CartItemMapper;
import com.yixuan.yh.product.mapper.ProductCarouselMapper;
import com.yixuan.yh.product.mapper.ProductMapper;
import com.yixuan.yh.product.mapper.ProductSkuMapper;
import com.yixuan.yh.product.mapper.multi.SkuMapper;
import com.yixuan.yh.product.mq.OrderExpirationMessage;
import com.yixuan.yh.product.pojo.model.entity.CartItem;
import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.model.entity.ProductCarousel;
import com.yixuan.yh.product.pojo.model.entity.ProductSku;
import com.yixuan.yh.product.pojo.model.multi.SkuSpecInfo;
import com.yixuan.yh.product.pojo.response.PartOfCartOrderResponse;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import com.yixuan.yh.product.service.ProductService;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class ProductServiceImpl extends BaseServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private ProductCarouselMapper productCarouselMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    @Qualifier("reserveStockScript")
    private RedisScript<Long> reserveStockScript;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public List<ProductSummaryResponse> getProducts() {
        return this.selectVoList(ProductSummaryResponse.class, w -> w
                        .eq(Product::getStatus, Product.ProductStatus.ON_SALE)
                        .apply("product_id >= (SELECT MIN(product_id) + FLOOR(RAND() * (MAX(product_id) - MIN(product_id))) FROM product)")
                        .last("LIMIT 5"))
                .stream()
                .map(response -> {
                    // 获取主规格信息
                    Long defaultSkuId = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                                    .select(Product::getDefaultSkuId)
                                    .eq(Product::getProductId, response.getProductId()))
                            .getDefaultSkuId();

                    BigDecimal price = productSkuMapper.selectOne(new LambdaQueryWrapper<ProductSku>()
                                    .select(ProductSku::getPrice)
                                    .eq(ProductSku::getSkuId, defaultSkuId))
                            .getPrice();

                    // 完善数据
                    response.setCoverUrl(awsUtils.generateAccessUrl(response.getCoverUrl()));
                    response.setPrice(price);

                    return response;
                }).toList();
    }

    @Override
    public ProductDetailResponse getDetailProducts(Long productId) {
        // 查询商品基础信息
        Product product = productMapper.selectPartOfDetail(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在！");
        }

        // 查询商品SKU
        List<ProductSku> productSkuList = productSkuMapper.selectByProductId(productId);
        List<ProductDetailResponse.Sku> skuList = null;
        if (!productSkuList.isEmpty()) {
            List<Long> skuIdList = productSkuList.stream().map(ProductSku::getSkuId).toList();
            List<SkuSpecInfo> skuSpecInfoList = skuMapper.selectSpecsBySkuIds(skuIdList);
            Map<Long, List<SkuSpecInfo>> specMap = skuSpecInfoList.stream()
                    .collect(Collectors.groupingBy(SkuSpecInfo::getSkuId));

            // 整合数据
            skuList = productSkuList.stream()
                    .map(
                            productSku -> ProductDetailResponse.Sku.builder()
                                    .skuId(productSku.getSkuId())
                                    .price(productSku.getPrice())
                                    .stock(productSku.getStock())
                                    .carousels(productCarouselMapper.selectList(new LambdaQueryWrapper<ProductCarousel>()
                                                    .select(ProductCarousel::getUrl)
                                                    .eq(ProductCarousel::getSkuId, productSku.getSkuId()))
                                            .stream()
                                            .map(carousel -> awsUtils.generateAccessUrl(carousel.getUrl()))
                                            .toList())
                                    .specs(specMap.getOrDefault(productSku.getSkuId(), emptyList()).stream()
                                            .map(skuSpecInfo -> new ProductDetailResponse.Spec(skuSpecInfo.getSpecKey(), skuSpecInfo.getSpecValue()))
                                            .toList())
                                    .build()
                    ).toList();
        }
        return ProductDetailResponse.builder()
                .productId(productId)
                .merchantId(product.getMerchantId())
                .defaultSkuId(product.getDefaultSkuId())
                .title(product.getTitle())
                .description(product.getDescription())
                .skus(skuList)
                .build();
    }

    @Override
    @Transactional
    public PartOfOrderResponse getPartOfOrder(Long orderId, Long productId, Long skuId, Integer quantity) throws BadRequestException, InterruptedException {
        // 检查 sku 是否属于对应 product
        if (!productId.equals(productSkuMapper.selectProductIdBySkuId(skuId))) {
            throw new BadRequestException("商品没有该规格！");
        }

        // 预占库存
        String skuStockKey = RedisConstant.SKU_STOCK_KEY_PREFIX + skuId;
        Long result = stringRedisTemplate.execute(reserveStockScript, Collections.singletonList(skuStockKey), quantity.toString());
        // 对应库存缓存不存在
        while (result != null && result.equals(RedisLuaResultConstant.RESERVE_STOCK_NO_KEY)) {
            RLock lock = redissonClient.getLock(RedisConstant.SKU_STOCK_LOCK_KEY_PREFIX + skuId);
            boolean isLock = lock.tryLock(1, TimeUnit.SECONDS);
            if (isLock) {
                try {
                    // 再次判断缓存是否存在
                    result = stringRedisTemplate.execute(reserveStockScript, Collections.singletonList(skuStockKey), quantity.toString());
                    assert result != null;
                    if (result.equals(RedisLuaResultConstant.RESERVE_STOCK_NO_KEY)) {
                        // 从数据库查询库存，并设置到缓存
                        Integer stock = productSkuMapper.selectStockBySkuId(skuId);
                        stringRedisTemplate.opsForValue().set(skuStockKey, stock.toString());
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                throw new BadRequestException("服务器繁忙，请重试！");
            }
            // 重新执行预占库存
            result = stringRedisTemplate.execute(reserveStockScript, Collections.singletonList(skuStockKey), quantity.toString());
        }
        // 库存不足
        assert result != null;
        if (result.equals(RedisLuaResultConstant.RESERVE_STOCK_ERROR)) {
            throw new BadRequestException("库存不足！");
        }

        // 数据库扣减库存（临时）
        productSkuMapper.updateStock(skuId, -quantity);

        // 此处可以无需写入本地信息表（因为如果数据库事务回滚了，后续可以在消费前先判断orderId是否存在，存在才去处理）
        try {
            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(new OrderExpirationMessage(orderId, skuId, quantity)))
                    .setHeader("x-delay", 1000 * 60 * 2)
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstant.ORDER_DELAY_EXCHANGE, RabbitMQConstant.ORDER_DELAY_QUEUE_KEY, message);
        } catch (Exception e) {
            stringRedisTemplate.opsForValue().decrement(skuStockKey, quantity);
            throw new BadRequestException("服务器异常！");
        }

        Product product = productMapper.selectPartOfOrder(productId);
        ProductSku productSku = productSkuMapper.selectPriceBySkuId(skuId);
        PartOfOrderResponse partOfOrderResponse = new PartOfOrderResponse();
        partOfOrderResponse.setMerchantId(product.getMerchantId());
        partOfOrderResponse.setProductName(product.getTitle());
        partOfOrderResponse.setPrice(productSku.getPrice());

        return partOfOrderResponse;
    }

    @Override
    @Transactional
    public Map<Long, PartOfCartOrderResponse> getPartOfCartOrder(Long orderId, List<Long> cartItemIdList) {
        // 查询所需数据
        List<CartItem> cartItemList = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .select(CartItem::getCartItemId, CartItem::getProductId, CartItem::getSkuId, CartItem::getQuantity, CartItem::getProductId, CartItem::getSkuId, CartItem::getSelectedSku)
                .in(CartItem::getCartItemId, cartItemIdList));

        if (cartItemList.isEmpty()) {
            return Collections.emptyMap();
        }

        // 删除购物车项
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .in(CartItem::getCartItemId, cartItemIdList));

        // 暂时复用
        return cartItemList.stream().collect(Collectors.toMap(
                CartItem::getCartItemId,
                cartItem -> {
                    try {
                        PartOfOrderResponse partOfOrderResponse = getPartOfOrder(orderId, cartItem.getProductId(), cartItem.getSkuId(), cartItem.getQuantity());
                        PartOfCartOrderResponse partOfCartOrderResponse = new PartOfCartOrderResponse();
                        partOfCartOrderResponse.setMerchantId(partOfOrderResponse.getMerchantId());
                        partOfCartOrderResponse.setPrice(partOfOrderResponse.getPrice());
                        partOfCartOrderResponse.setProductName(partOfOrderResponse.getProductName());
                        partOfCartOrderResponse.setQuantity(cartItem.getQuantity());
                        partOfCartOrderResponse.setProductId(cartItem.getProductId());
                        partOfCartOrderResponse.setSkuId(cartItem.getSkuId());
                        partOfCartOrderResponse.setSku(cartItem.getSelectedSku());
                        return partOfCartOrderResponse;
                    } catch (BadRequestException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));
    }

    @Override
    public void putReservedStock(Long skuId, Integer quantity) {
        stringRedisTemplate.opsForValue().increment(RedisConstant.SKU_STOCK_KEY_PREFIX + skuId, quantity);
    }

    @Override
    @Transactional
    public void increaseSalesVolume(Map<Long, Integer> productQuantityMap) {
        if (productQuantityMap == null || productQuantityMap.isEmpty()) {
            return;
        }

        productQuantityMap.forEach((productId, quantity) -> {
            if (productId == null || quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Invalid product sales volume increment.");
            }
            int affectedRows = productMapper.increaseSalesVolume(productId, quantity);
            if (affectedRows == 0) {
                throw new IllegalArgumentException("Product does not exist.");
            }
        });
    }
}
