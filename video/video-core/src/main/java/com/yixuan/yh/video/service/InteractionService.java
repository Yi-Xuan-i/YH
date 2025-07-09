package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;

public interface InteractionService {
    void like(Long userId, Long videoId) throws Exception;

    void unlike(Long userId, Long videoId) throws Exception;

    void favorite(Long userId, Long videoId) throws Exception;

    void unfavorite(Long userId, Long videoId) throws Exception;

    void likeBatch(VideoInteractionBatchRequest videoInteractionBatchRequest);

    void favoriteBatch(VideoInteractionBatchRequest videoInteractionBatchRequest);
}
