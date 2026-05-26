package com.yixuan.yh.chat.controller;

import com.yixuan.yh.chat.request.ChatMediaPresignRequest;
import com.yixuan.yh.chat.request.ChatMediaUploadCallbackRequest;
import com.yixuan.yh.chat.response.ChatMediaPresignResponse;
import com.yixuan.yh.chat.response.ChatMessageMediaResponse;
import com.yixuan.yh.chat.service.ChatMessageMediaService;
import com.yixuan.yh.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChatMessageMedia")
@RestController
@RequestMapping("/me/message-media")
public class ChatMessageMediaController {

    @Autowired
    private ChatMessageMediaService chatMessageMediaService;

    @Operation(summary = "Get direct upload url for chat image or video")
    @PostMapping("/presign-put-object")
    public Result<ChatMediaPresignResponse> presignPutObject(@RequestBody ChatMediaPresignRequest request) {
        return Result.success(chatMessageMediaService.presignPutObject(request));
    }

    @Operation(summary = "Chat image or video upload callback")
    @PostMapping("/upload-callback")
    public Result<ChatMessageMediaResponse> uploadCallback(@RequestParam Long mediaId) {
        return Result.success(chatMessageMediaService.uploadCallback(mediaId));
    }
}
