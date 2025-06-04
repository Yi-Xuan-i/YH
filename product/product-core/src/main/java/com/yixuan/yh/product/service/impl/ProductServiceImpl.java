package com.yixuan.yh.product.service.impl;

import com.yixuan.yh.product.mapper.ProductCarouselMapper;
import com.yixuan.yh.product.mapper.ProductMapper;
import com.yixuan.yh.product.mapper.ProductSkuMapper;
import com.yixuan.yh.product.mapper.multi.SkuMapper;
import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.model.entity.ProductSku;
import com.yixuan.yh.product.pojo.model.multi.SkuSpecInfo;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import com.yixuan.yh.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        productDetailResponse.setProductId(productId);
        productDetailResponse.setMerchantId(product.getMerchantId());
        productDetailResponse.setTitle(product.getTitle());
        productDetailResponse.setDescription(product.getDescription());

        // 查询商品轮播图
        productDetailResponse.setCarousels(productCarouselMapper.selectUrlByProductId(productId));

        // 查询商品SKU
        List<ProductSku> productSkuList = productSkuMapper.selectByProductId(productId);
        if (!productSkuList.isEmpty()) {
            List<Long> skuIdList = productSkuList.stream().map(ProductSku::getSkuId).toList();
            List<SkuSpecInfo> skuSpecInfoList = skuMapper.selectSpecsBySkuIds(skuIdList);
            Map<Long, List<SkuSpecInfo>> specMap = skuSpecInfoList.stream()
                    .collect(Collectors.groupingBy(SkuSpecInfo::getSkuId));

            // 整合数据
            List<ProductDetailResponse.Sku> skuList = productSkuList.stream()
                    .map(productSku -> new ProductDetailResponse.Sku(
                            productSku.getSkuId(),
                            productSku.getPrice(),
                            productSku.getStock(),
                            specMap.getOrDefault(productSku.getSkuId(), emptyList()).stream()
                                    .map(skuSpecInfo -> new ProductDetailResponse.Spec(skuSpecInfo.getSpecKey(), skuSpecInfo.getSpecValue()))
                                    .toList()
                    )).toList();
            productDetailResponse.setSkus(skuList);

        }
        return productDetailResponse;
    }
}
