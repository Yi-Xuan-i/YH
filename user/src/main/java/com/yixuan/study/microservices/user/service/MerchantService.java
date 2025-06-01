package com.yixuan.study.microservices.user.service;

import com.yixuan.study.microservices.user.request.PostMerchantRequest;
import com.yixuan.study.microservices.user.response.MerchantBasicDataResponse;

import java.io.IOException;

public interface MerchantService {
    void postMerchant(Long userId, PostMerchantRequest postMerchantRequest) throws IOException;

    MerchantBasicDataResponse getBasicMerchant(Long userId);
}
