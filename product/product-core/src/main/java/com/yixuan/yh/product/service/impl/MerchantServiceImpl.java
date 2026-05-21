package com.yixuan.yh.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.order.feign.OrderPrivateClient;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import com.yixuan.yh.product.mapper.*;
import com.yixuan.yh.product.mapper.multi.SkuMapper;
import com.yixuan.yh.product.mapstruct.MerchantMapStruct;
import com.yixuan.yh.product.pojo.model.entity.*;
import com.yixuan.yh.product.pojo.model.multi.SkuSpecInfo;
import com.yixuan.yh.product.pojo.request.*;
import com.yixuan.yh.product.pojo.response.MerchantProductStatsResponse;
import com.yixuan.yh.product.pojo.response.ProductEditResponse;
import com.yixuan.yh.product.pojo.response.ProductManageItemResponse;
import com.yixuan.yh.product.service.MerchantService;
import com.yixuan.yh.product.service.SpecKeyService;
import com.yixuan.yh.product.service.SpecValueService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpecKeyMapper specKeyMapper;
    @Autowired
    private SpecValueMapper specValueMapper;
    @Autowired
    private SkuSpecMapper skuSpecMapper;
    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private ProductCarouselMapper productCarouselMapper;
    @Autowired
    private OrderPrivateClient orderPrivateClient;

    @Override
    public void putMerchantProductStatus(Long productId, PutProductStatusRequest putProductStatusRequest) {
        Product product = new Product();
        product.setProductId(productId);
        product.setStatus(Product.ProductStatus.getByCode(putProductStatusRequest.getStatus()));
        productMapper.updateById(product);
    }

    @Override
    public List<ProductManageItemResponse> getMerchantProduct(Long user) {
        return productMapper.selectMerchantProducts(user);
    }

    @Override
    public MerchantProductStatsResponse getMerchantProductStats(Long userId) {
        List<Long> productIdList = productMapper.selectProductIdsByMerchantId(userId);

        MerchantProductStatsResponse response = new MerchantProductStatsResponse();
        response.setProductCount(productIdList.size());
        response.setDailySalesVolume(0);
        response.setDailySalesAmount(BigDecimal.ZERO);
        if (productIdList.isEmpty()) {
            return response;
        }

        Result<MerchantDailySalesResponse> result = orderPrivateClient.getMerchantDailySales(productIdList);
        MerchantDailySalesResponse dailySales = result == null ? null : result.getData();
        if (dailySales != null) {
            response.setDailySalesVolume(dailySales.getDailySalesVolume());
            response.setDailySalesAmount(dailySales.getDailySalesAmount());
        }
        return response;
    }


    @Override
    public List<ProductManageItemResponse> getMerchantOnSaleProduct(Long userId) {
//        productMapper.selectList(new LambdaQueryWrapper<Product>()
//                .select(Product::getProductId, Product::getTitle, Product::getCoverUrl, Product::getPrice, Product::getSalesVolume)
//                .eq(Product::getMerchantId, userId)
//                .eq(Product::getStatus, Product.ProductStatus.ON_SALE));
        return null;
    }

    @Override
    public ProductEditResponse getMerchantProductEditData(Long productId) {
        Product product = productMapper.selectEditBasicData(productId);
        if (product == null) {
            throw new YHClientException("商品不存在！");
        }

        ProductEditResponse productEditResponse = new ProductEditResponse();
        // 商品基本信息
        productEditResponse.setTitle(product.getTitle());
        productEditResponse.setDescription(product.getDescription());
        productEditResponse.setCoverUrl(awsUtils.generateAccessUrl(product.getCoverUrl()));
        productEditResponse.setDefaultSkuId(product.getDefaultSkuId());
        // 商品SKU
        List<ProductSku> skuList = productSkuMapper.selectByProductId(productId);
        if (!skuList.isEmpty()) {
            List<Long> skuIdList = skuList.stream().map(ProductSku::getSkuId).toList();

            List<SkuSpecInfo> specList = skuMapper.selectSpecsBySkuIds(skuIdList);

            Map<Long, List<SkuSpecInfo>> specMap = specList.stream()
                    .collect(Collectors.groupingBy(SkuSpecInfo::getSkuId));

            List<ProductEditResponse.SkuDetailDTO> skuDetailDTOList = skuList.stream()
                    .map(sku -> new ProductEditResponse.SkuDetailDTO(
                            sku.getSkuId(),
                            sku.getPrice(),
                            sku.getStock(),
                            productCarouselMapper.selectList(new LambdaQueryWrapper<ProductCarousel>()
                                            .select(ProductCarousel::getId, ProductCarousel::getUrl)
                                            .eq(ProductCarousel::getSkuId, sku.getSkuId()))
                                    .stream()
                                    .map(productCarousel -> {
                                        ProductEditResponse.SkuDetailDTO.Carousel carousel = new ProductEditResponse.SkuDetailDTO.Carousel();
                                        carousel.setId(productCarousel.getId());
                                        carousel.setUrl(awsUtils.generateAccessUrl(productCarousel.getUrl()));
                                        return carousel;
                                    })
                                    .toList(),
                            specMap.getOrDefault(sku.getSkuId(), emptyList()).stream()
                                    .map(skuSpecInfo -> new ProductEditResponse.SkuDetailDTO.SpecPair(skuSpecInfo.getSpecKey(), skuSpecInfo.getSpecValue(), skuSpecInfo.getSpecKeyId(), skuSpecInfo.getSpecValueId()))
                                    .toList()
                    )).toList();
            productEditResponse.setProductSkuList(skuDetailDTOList);
        }

        return productEditResponse;
    }

    @Override
    public String getEditUploadVideoPresignedUrl(Long userId) {
        // 这里应该还要插入一条记录辅助用于清理视频或者也可以采用桶生命周期规则自动清理
        return awsUtils.presignPutObject(awsUtils.generateKey(), "video/mp4", Duration.ofMinutes(10));
    }

    @Override
    public String getEditUploadImagePresignedUrl(Long userId) {
        return awsUtils.presignPutObject(awsUtils.generateKey(), "image/png", Duration.ofMinutes(10));
    }

    @Override
    @Transactional
    public void postSkuSpec(PostSkuSpecRequest postSkuSpecRequest) {
        if (postSkuSpecRequest.getType().equals("new")) {
            /* 代表新增规格此时不会产生新的 SKU（除非没有一个 SKU），但是会让每个 SKU 新增一对 Spec。*/
            PostSkuSpecRequest.Spec spec = postSkuSpecRequest.getSpec();
            // 插入规格键和值
            Long keyId = snowflakeUtils.nextId();
            Long valueId = snowflakeUtils.nextId();
            specKeyMapper.insert(keyId, spec.getKey());
            specValueMapper.insert(valueId, spec.getValue());
            // 获取 product 的所有 SKU
            List<Long> skuIdList = productSkuMapper.selectSkuIdByProductId(postSkuSpecRequest.getProductId());
            if (skuIdList.isEmpty()) { // 代表原先没有任何 SKU
                Long skuId = snowflakeUtils.nextId();
                skuIdList.add(skuId);
                productSkuMapper.insertBatch(postSkuSpecRequest.getProductId(), skuIdList);
            }
            // 当前 product 的每个 SKU 新增规格键值对
            skuSpecMapper.insertBatch1(skuIdList, keyId, valueId);
        } else {
            /* 代表不是新增规格，此时会新增 SKU */
            PostSkuSpecRequest.Spec spec = postSkuSpecRequest.getSpec();
            // 插入新规格值
            Long valueId = snowflakeUtils.nextId();
            specValueMapper.insert(valueId, spec.getValue());
            // 生成新的 SKU 的 id
            List<Long> skuIdList = new ArrayList<>(postSkuSpecRequest.getSkus().size());
            for (int i = 0; i < postSkuSpecRequest.getSkus().size(); i++) {
                skuIdList.add(snowflakeUtils.nextId());
            }
            // 遍历所有 SKU 填充缺失的 id
            for (List<PostSkuSpecRequest.SpecId> sku : postSkuSpecRequest.getSkus()) {
                for (PostSkuSpecRequest.SpecId specId : sku) {
                    if (specId.getValueId() == null) {
                        specId.setValueId(valueId);
                    }
                }
            }
            // 插入规格键值对
            skuSpecMapper.insertBatch2(skuIdList, postSkuSpecRequest.getSkus());
            // 建立 Product 与 SKU 的映射关系
            productSkuMapper.insertBatch(postSkuSpecRequest.getProductId(), skuIdList);
        }
    }

    @Override
    public void putSkuMain(Long productId, PutSkuMainRequest putSkuMainRequest) {
        // 判断该 SKU 是否真的属于该 Product
        ProductSku productSku = productSkuMapper.selectOne(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getSkuId, putSkuMainRequest.getSkuId())
                .eq(ProductSku::getProductId, productId));
        if (productSku == null) {
            throw new YHClientException("该 SKU 不属于该 Product！");
        }

        // 执行修改
        Product product = new Product();
        product.setProductId(productId);
        product.setDefaultSkuId(putSkuMainRequest.getSkuId());
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void deleteMerchantProduct(Long productId) {
        // 删除商品基本信息
        productMapper.deleteByProductId(productId);
        // 获取商品的所有 SKU Id
        List<Long> skuIdList = productSkuMapper.selectSkuIdByProductId(productId);
        // 删除商品的 SKU
        productSkuMapper.deleteBatch(skuIdList);
        // 获取商品的所有 Key、Value Id
        List<SkuSpec> skuSpecList = skuSpecMapper.selectKeyValueBatch(skuIdList);
        List<Long> keyIdList = new ArrayList<>(skuSpecList.size());
        List<Long> valueIdList = new ArrayList<>(skuSpecList.size());
        for (SkuSpec skuSpec : skuSpecList) {
            keyIdList.add(skuSpec.getKeyId());
            valueIdList.add(skuSpec.getValueId());
        }
        // 删除商品的 SKU 关联的 Spec
        skuSpecMapper.deleteBatch(skuIdList);
        // 删除 Spec Key
        specKeyMapper.deleteBatch(keyIdList);
        // 删除 Spec Value
        specValueMapper.deleteBatch(valueIdList);
    }

    @Override
    public void putMerchantProductBasicInfo(Long productId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        Product product = MerchantMapStruct.INSTANCE.putProductBasicInfoRequestToProduct(putProductBasicInfoRequest);
        product.setProductId(productId);
        if (putProductBasicInfoRequest.getCover() != null) {
            product.setCoverUrl(awsUtils.putObject(putProductBasicInfoRequest.getCover()));
        }
        productMapper.updateBasicInfo(product);
    }

    @Override
    public void postMerchantProduct(Long merchantId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        Product product = MerchantMapStruct.INSTANCE.putProductBasicInfoRequestToProduct(putProductBasicInfoRequest);
        product.setMerchantId(merchantId);
        product.setStatus(Product.ProductStatus.OFF_SHELF); // 审核功能未实现，暂时默认新建商品为下架状态
        if (putProductBasicInfoRequest.getCover() != null) {
            product.setCoverUrl(awsUtils.putObject(putProductBasicInfoRequest.getCover()));
        }
        productMapper.insert(product);
    }

    @Override
    public void putSku(Long userId, PutSkuRequest putSkuRequest) throws BadRequestException {
        // 鉴权
        List<Long> skuIdList = putSkuRequest.getChangeSkuList().stream().map(PutSkuRequest.PutSku::getSkuId).toList();
        List<Long> merchantIdList;
        if ((merchantIdList = skuMapper.selectMerchantIdBySkuIds(skuIdList)).size() > 1 || !merchantIdList.get(0).equals(userId)) {
            throw new BadRequestException("你没有权限！");
        }

        List<ProductSku> productSkuList = new ArrayList<>(putSkuRequest.getChangeSkuList().size());
        for (PutSkuRequest.PutSku putSku : putSkuRequest.getChangeSkuList()) {
            productSkuList.add(MerchantMapStruct.INSTANCE.putSkuRequestToProductSku(putSku));
        }

        productSkuMapper.update(productSkuList);
    }

    @Override
    public void postSkuCarousel(Long skuId, PostSkuCarouselRequest postSkuCarouselRequest) throws IOException {
        // 查询对应 ProductId
        Long productId = productSkuMapper.selectProductIdBySkuId(skuId);
        if (productId == null) {
            throw new YHClientException("SKU不存在！");
        }

        // 上传图片
        String url = awsUtils.putObject(postSkuCarouselRequest.getFile());

        // 插入数据库
        ProductCarousel productCarousel = new ProductCarousel();
        productCarousel.setSkuId(skuId);
        productCarousel.setUrl(url);
        productCarouselMapper.insert(productCarousel);
    }

    @Override
    public void deleteSkuCarousel(Long carouselId) {
        productCarouselMapper.deleteById(carouselId);
    }

    @Override
    public List<String> getSkuCarousels(Long skuId) {
        return productCarouselMapper.selectList(new LambdaQueryWrapper<ProductCarousel>()
                        .select(ProductCarousel::getUrl)
                        .eq(ProductCarousel::getSkuId, skuId))
                .stream()
                .map(carousel -> awsUtils.generateAccessUrl(carousel.getUrl()))
                .toList();
    }
}
