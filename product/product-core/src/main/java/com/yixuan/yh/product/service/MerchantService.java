package com.yixuan.yh.product.service;

import com.yixuan.yh.product.pojo.request.PostCarouselRequest;
import com.yixuan.yh.product.pojo.request.PostSkuSpecRequest;
import com.yixuan.yh.product.pojo.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.pojo.request.PutSkuRequest;
import com.yixuan.yh.product.pojo.response.ProductEditResponse;
import com.yixuan.yh.product.pojo.response.ProductManageItemResponse;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;

public interface MerchantService {
    List<ProductManageItemResponse> getMerchantProduct(Long user);

    ProductEditResponse getMerchantProductEditData(Long productId);

    void postSkuSpec(PostSkuSpecRequest postSkuSpecRequest);

    void deleteMerchantProduct(Long productId);

    void putMerchantProductBasicInfo(Long productId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;

    void postMerchantProduct(Long merchantId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;

    void putSku(Long userId, PutSkuRequest putSkuRequest) throws BadRequestException;

    void postCarousel(Long userId, Long productId, PostCarouselRequest postCarouselRequest) throws IOException;

    void deleteCarousel(Long userId, Long productId, Long carouselId) throws BadRequestException;
}
