package com.yixuan.yh.chat.service;

import com.yixuan.yh.chat.entity.ChatMessageMedia;
import com.yixuan.yh.chat.request.ChatMediaPresignRequest;
import com.yixuan.yh.chat.request.ChatMediaUploadCallbackRequest;
import com.yixuan.yh.chat.response.ChatMediaPresignResponse;
import com.yixuan.yh.chat.response.ChatMessageMediaResponse;

public interface ChatMessageMediaService {
    ChatMediaPresignResponse presignPutObject(ChatMediaPresignRequest request);

    ChatMessageMediaResponse uploadCallback(Long mediaId);

    ChatMessageMediaResponse toResponse(ChatMessageMedia media);
}
