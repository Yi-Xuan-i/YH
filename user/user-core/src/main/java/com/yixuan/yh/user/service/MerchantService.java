package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.request.PostMerchantRequest;
import com.yixuan.yh.user.pojo.request.PutMerchantRequest;
import com.yixuan.yh.user.pojo.response.MerchantBasicDataResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MerchantService {
    void postMerchant(Long userId, PostMerchantRequest postMerchantRequest) throws IOException;

    MerchantBasicDataResponse getBasicMerchant(Long userId);

    void putMerchant(Long userId, PutMerchantRequest putMerchantRequest) throws IOException;

    String postAvatar(Long userId, MultipartFile avatar) throws IOException;
}
