package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.request.PostMerchantRequest;
import com.yixuan.yh.user.pojo.response.MerchantBasicDataResponse;

import java.io.IOException;

public interface MerchantService {
    void postMerchant(Long userId, PostMerchantRequest postMerchantRequest) throws IOException;

    MerchantBasicDataResponse getBasicMerchant(Long userId);
}
