package com.yixuan.yh.product.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.product.constant.RabbitMQConstant;
import com.yixuan.yh.product.constant.RedisKeyConstant;
import com.yixuan.yh.product.constant.RedisLuaResultConstant;
import com.yixuan.yh.product.mapper.ProductCarouselMapper;
import com.yixuan.yh.product.mapper.ProductMapper;
import com.yixuan.yh.product.mapper.ProductSkuMapper;
import com.yixuan.yh.product.mapper.multi.SkuMapper;
import com.yixuan.yh.product.mq.OrderExpirationMessage;
import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.model.entity.ProductSku;
import com.yixuan.yh.product.pojo.model.multi.SkuSpecInfo;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class ProductServiceImpl implements ProductService {

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
    @Qualifier("reserveStockScript")
    private RedisScript<Long> reserveStockScript;

    @Override
    public List<ProductSummaryResponse> getProducts() {
        return productMapper.selectList();
    }

    @Override
    public ProductDetailResponse getDetailProducts(Long productId) {
        // 查询商品基础信息
        Product product = productMapper.selectPartOfDetail(productId);
        if (product == null) {
            return null;
        }

        // 查询商品轮播图
        List<String> carouselList = productCarouselMapper.selectUrlByProductId(productId);

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
                                    .specs(specMap.getOrDefault(productSku.getSkuId(), emptyList()).stream()
                                            .map(skuSpecInfo -> new ProductDetailResponse.Spec(skuSpecInfo.getSpecKey(), skuSpecInfo.getSpecValue()))
                                            .toList())
                                    .build()
                    ).toList();
        }
        return ProductDetailResponse.builder()
                .productId(productId)
                .merchantId(product.getMerchantId())
                .title(product.getTitle())
                .description(product.getDescription())
                .carousels(carouselList)
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
        String skuStockKey = RedisKeyConstant.SKU_STOCK_KEY_PREFIX + skuId;
        Long result = stringRedisTemplate.execute(reserveStockScript, Collections.singletonList(skuStockKey), quantity.toString());
        if (result == null) {
            throw new RuntimeException("服务器异常！");
        }
        if (result.equals(RedisLuaResultConstant.RESERVE_STOCK_NO_KEY)) {
            RLock lock = redissonClient.getLock(RedisKeyConstant.SKU_STOCK_LOCK_KEY_PREFIX + skuId);
            boolean isLock = lock.tryLock(1, TimeUnit.SECONDS);
            if (isLock) {
                try {
                    // 从数据库查询库存，并设置到缓存
                    Integer stock = productSkuMapper.selectStockBySkuId(skuId);
                    stringRedisTemplate.opsForValue().set(skuStockKey, stock.toString());
                } finally {
                    lock.unlock();
                }
            } else {
                throw new BadRequestException("服务器繁忙，请重试！");
            }
            // 重新执行预占库存
            result = stringRedisTemplate.execute(reserveStockScript, Collections.singletonList(skuStockKey), quantity.toString());
            if (result == null) {
                throw new RuntimeException("服务器异常！");
            }
        }
        if (result.equals(RedisLuaResultConstant.RESERVE_STOCK_ERROR)) {
            throw new BadRequestException("库存不足！");
        }

        try {
            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(new OrderExpirationMessage(orderId, skuId, quantity)))
                    .setHeader("x-delay", 1000 * 60 * 15)
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
    public void putReservedStock(Long skuId, Integer quantity) {
        stringRedisTemplate.opsForValue().increment(RedisKeyConstant.SKU_STOCK_KEY_PREFIX + skuId, quantity);
    }
}
