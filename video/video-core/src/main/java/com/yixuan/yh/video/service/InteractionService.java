package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;

public interface InteractionService {
    void like(Long userId, Long videoId) throws Exception;

    void unlike(Long userId, Long videoId) throws Exception;

    void likeBatch(VideoLikeBatchRequest videoLikeBatchRequest);
}
