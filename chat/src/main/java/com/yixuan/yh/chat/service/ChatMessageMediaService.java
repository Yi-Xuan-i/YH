package com.yixuan.yh.chat.service;

import com.yixuan.yh.chat.entity.ChatMessageMedia;
import com.yixuan.yh.chat.request.ChatMediaPresignRequest;
import com.yixuan.yh.chat.response.ChatMediaPresignResponse;
import com.yixuan.yh.chat.response.ChatMessageMediaResponse;

public interface ChatMessageMediaService {
    /**
     * 获取聊天媒体文件直传地址，支持图片、视频、语音。
     */
    ChatMediaPresignResponse presignPutObject(ChatMediaPresignRequest request);

    ChatMessageMediaResponse uploadCallback(Long mediaId);

    ChatMessageMediaResponse toResponse(ChatMessageMedia media);
}
