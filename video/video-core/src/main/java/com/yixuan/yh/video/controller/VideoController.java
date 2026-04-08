package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.request.GetPresignUrlRequest;
import com.yixuan.yh.video.pojo.response.*;
import com.yixuan.yh.video.pojo.request.PostVideoMessageRequest;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Video")
@RestController
@RequestMapping("/me")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Operation(summary = "初始化分片上传")
    @PostMapping("/start-upload-part")
    public Result<String> startUploadPart(Integer totalChunks) {
        return Result.success(videoService.startUploadPart(UserContext.getUser(), totalChunks));
    }

    @Operation(summary = "获取分片直传URL")
    @PostMapping("/presign-upload-part")
    public Result<Map<Integer, String>> presignUploadPart(@RequestBody GetPresignUrlRequest getPresignUrlRequest) {
        return Result.success(videoService.presignUploadPart(UserContext.getUser(), getPresignUrlRequest));
    }

    @Operation(summary = "获取普通直传URL")
    @PostMapping("/presign-put-object")
    public Result<PresignPutObjectResponse> presignPutObject() {
        return Result.success(videoService.presignPutObject(UserContext.getUser()));
    }

    @Operation(summary = "通知上传完毕")
    @PostMapping("/end")
    public Result<PostVideoEndResponse> postVideoEnd(Long taskId) {
        return Result.success(videoService.postVideoEnd(UserContext.getUser(), taskId));
    }

    @Operation(summary = "正式上传视频（提交视频关联信息）")
    @PostMapping
    public Result<Void> postVideoMessage(@ModelAttribute PostVideoMessageRequest postVideoMessageRequest) throws Exception {
        videoService.postVideoMessage(UserContext.getUser(), postVideoMessageRequest);
        return Result.success();
    }

    @Operation(summary = "获取自己已发布的视频")
    @GetMapping("/published")
    public Result<List<GetPublishedVideoResponse>> getPublishedVideo(@RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getPublishedVideo(UserContext.getUser(), lastMinId));
    }

    @Operation(summary = "获取自己审核中的视频")
    @GetMapping("/processing")
    public Result<List<GetProcessingVideoResponse>> getProcessingVideo(@RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getProcessingVideo(UserContext.getUser(), lastMinId));
    }

    @Operation(summary = "获取自己未通过的视频")
    @GetMapping("/rejected")
    public Result<List<GetRejectedVideoResponse>> getRejectedVideo(@RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getRejectedVideo(UserContext.getUser(), lastMinId));
    }

    @Operation(summary = "获取自己喜欢的视频")
    @GetMapping("/like")
    public Result<List<GetLikeVideoResponse>> getLikeVideo(@RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getLikeVideo(UserContext.getUser(), lastMinId));
    }

    @Operation(summary = "获取自己收藏的视频")
    @GetMapping("/favorite")
    public Result<List<GetFavoriteVideoResponse>> getFavoriteVideo(@RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getFavoriteVideo(UserContext.getUser(), lastMinId));
    }
}

