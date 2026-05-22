package com.yixuan.yh.live.service;

import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.LiveProductItemResponse;

import java.util.List;

public interface LiveProductService {
    void postMerchantProduct(Long userId, PostLiveProductRequest postLiveProductRequest);

    List<LiveProductItemResponse> getRoomLiveProduct(Long roomId);

    LiveProductItemResponse getLiveProduct(Long productId);
}
