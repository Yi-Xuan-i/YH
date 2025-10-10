package com.yixuan.yh.video.service;

import com.yixuan.yh.video.pojo.entity.multi.CommentWithReceiver;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.pojo.response.GetDirectCommentResponse;
import com.yixuan.yh.video.pojo.response.GetReplyCommentResponse;
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

    List<GetDirectCommentResponse> directComment(Long videoId);

    List<GetReplyCommentResponse> replyComment(Long commentId);
}
