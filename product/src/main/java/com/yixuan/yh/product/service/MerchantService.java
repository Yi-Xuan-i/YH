package com.yixuan.yh.product.service;

import com.yixuan.yh.product.request.PostSkuSpecRequest;
import com.yixuan.yh.product.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.response.ProductEditResponse;
import com.yixuan.yh.product.response.ProductManageItemResponse;

import java.io.IOException;
import java.util.List;

public interface MerchantService {
    List<ProductManageItemResponse> getMerchantProduct(Long user);

    ProductEditResponse getMerchantProductEditData(Long productId);

    void postSkuSpec(PostSkuSpecRequest postSkuSpecRequest);

    void deleteMerchantProduct(Long productId);

    void putMerchantProductBasicInfo(Long productId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;

    void postMerchantProduct(Long merchantId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException;
}
