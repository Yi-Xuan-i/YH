package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.response.GetProcessingVideoResponse;
import com.yixuan.yh.video.pojo.response.GetPublishedVideoResponse;
import com.yixuan.yh.video.pojo.response.GetRejectedVideoResponse;
import com.yixuan.yh.video.pojo.request.PostVideoMessageRequest;
import com.yixuan.yh.video.pojo.response.VideoMainWithInteractionResponse;
import com.yixuan.yh.video.service.VideoService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Tag(name = "Video")
@RestController
@RequestMapping("/me")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Operation(summary = "发起上传")
    @PostMapping("/start")
    public Result<String> postVideoStart(Long fileSize, Integer totalChunks) {
        return Result.success(videoService.postVideoStart(UserContext.getUser(), fileSize, totalChunks));
    }

    @Operation(summary = "上传视频（上传视频文件数据）")
    @PostMapping("/part")
    public Result<Void> postVideo(Long uploadId, Long partNumber, MultipartFile file) throws Exception {
        videoService.postVideo(UserContext.getUser(), uploadId, partNumber, file);
        return Result.success();
    }

    @Operation(summary = "合并视频")
    @PostMapping("/end")
    public Result<Void> postVideoEnd(Long uploadId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        videoService.postVideoEnd(uploadId);
        return Result.success();
    }

    @Operation(summary = "正式上传视频（提交视频关联信息）")
    @PostMapping
    public Result<Void> postVideoMessage(@ModelAttribute PostVideoMessageRequest postVideoMessageRequest) throws IOException, InterruptedException {
        videoService.postVideoMessage(UserContext.getUser(), postVideoMessageRequest);
        return Result.success();
    }

    @Operation(summary = "获取视频（已登入）")
    @GetMapping("/list")
    public Result<List<VideoMainWithInteractionResponse>> getVideosWithInteractionStatus() {
        return Result.success(videoService.getVideosWithInteractionStatus(UserContext.getUser()));
    }

    @Operation(summary = "获取自己已发布的视频")
    @GetMapping("/published")
    public Result<List<GetPublishedVideoResponse>> getPublishedVideo() {
        return Result.success(videoService.getPublishedVideo(UserContext.getUser()));
    }

    @Operation(summary = "获取自己审核中的视频")
    @GetMapping("/processing")
    public Result<List<GetProcessingVideoResponse>> getProcessingVideo() {
        return Result.success(videoService.getProcessingVideo(UserContext.getUser()));
    }

    @Operation(summary = "获取自己未通过的视频")
    @GetMapping("/rejected")
    public Result<List<GetRejectedVideoResponse>> getRejectedVideo() {
        return Result.success(videoService.getRejectedVideo(UserContext.getUser()));
    }
}

