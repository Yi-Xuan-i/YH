package com.yixuan.yh.live.service;

import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.GetLiveProductResponse;
import com.yixuan.yh.live.response.PostLiveProductResponse;

import java.io.IOException;
import java.util.List;

public interface LiveProductService {
    PostLiveProductResponse postLiveProduct(Long userId, PostLiveProductRequest postLiveProductRequest) throws IOException;

    List<GetLiveProductResponse> getRoomLiveProduct(Long roomId);

    GetLiveProductResponse getLiveProduct(Long id);
}
