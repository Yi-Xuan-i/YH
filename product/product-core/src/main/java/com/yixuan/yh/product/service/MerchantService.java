package com.yixuan.yh.product.service;

import com.yixuan.yh.product.pojo.request.*;
import com.yixuan.yh.product.pojo.response.MerchantProductStatsResponse;
import com.yixuan.yh.product.pojo.response.ProductEditResponse;
import com.yixuan.yh.product.pojo.response.ProductManageItemResponse;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;

public interface MerchantService {
    void putMerchantProductStatus(Long productId, PutProductStatusRequest putProductStatusRequest);

    List<ProductManageItemResponse> getMerchantProduct(Long user);

    MerchantProductStatsResponse getMerchantProductStats(Long userId);

    List<ProductManageItemResponse> getMerchantOnSaleProduct(Long userId);

    ProductEditResponse getMerchantProductEditData(Long productId);

    String getEditUploadVideoPresignedUrl(Long userId);

    String getEditUploadImagePresignedUrl(Long userId);

    void postSkuSpec(PostSkuSpecRequest postSkuSpecRequest);

    void deleteSkuSpec(Long productId, DeleteSkuSpecRequest deleteSkuSpecRequest);

    void putSkuMain(Long productId, PutSkuMainRequest putSkuMainRequest);

    void deleteMerchantProduct(Long productId);

    void putMerchantProductBasicInfo(Long productId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;

    void postMerchantProduct(Long merchantId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;

    void putSku(Long userId, PutSkuRequest putSkuRequest) throws BadRequestException;

    void postSkuCarousel(Long skuId, PostSkuCarouselRequest postSkuCarouselRequest) throws IOException;

    void deleteSkuCarousel(Long carouselId);

    List<String> getSkuCarousels(Long skuId);
}
