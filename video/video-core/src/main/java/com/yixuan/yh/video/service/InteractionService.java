package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.pojo.response.GetCommentResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface InteractionService {
    void like(Long userId, Long videoId) throws Exception;

    void unlike(Long userId, Long videoId) throws Exception;

    void favorite(Long userId, Long videoId) throws Exception;

    void unfavorite(Long userId, Long videoId) throws Exception;

    void likeBatch(VideoInteractionBatchRequest videoInteractionBatchRequest);

    void favoriteBatch(VideoInteractionBatchRequest videoInteractionBatchRequest);

    String comment(Long videoId, Long userId, PostCommentRequest postCommentRequest) throws BadRequestException;

    List<GetCommentResponse> directComment(Long videoId);
}
