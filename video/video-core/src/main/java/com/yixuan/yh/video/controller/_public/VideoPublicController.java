package com.yixuan.yh.video.controller._public;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.response.GetPublishedVideoResponse;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.pojo.response.VideoSearchResponse;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Video")
@RestController
@RequestMapping("/public")
public class VideoPublicController {

    @Autowired
    private VideoService videoService;

    @Operation(summary = "获取视频（刷视频时继续加载视频）")
    @GetMapping("/list")
    public Result<List<VideoMainResponse>> getVideos() {
        return Result.success(videoService.getVideos(UserContext.getUser()));
    }

    @Operation(summary = "获取指定视频数据")
    @GetMapping
    public Result<VideoMainResponse> getVideo(@RequestParam Long videoId) {
        return Result.success(videoService.getVideo(videoId));
    }

    @Operation(summary = "获取他人已发布的作品")
    @GetMapping("/published")
    public Result<List<GetPublishedVideoResponse>> getPublishedVideo(
            @RequestParam Long creatorId,
            @RequestParam(required = false) Long lastMinId) {
        return Result.success(videoService.getPublishedVideo(creatorId, lastMinId));
    }

    @Operation(summary = "搜索视频")
    @GetMapping("/search")
    public Result<List<VideoSearchResponse>> searchVideos(@RequestParam String keyword) {
        return Result.success(videoService.searchVideos(keyword));
    }
}
