package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.request.PostVideoRequest;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.pojo.response.VideoMainWithInteractionResponse;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Video")
@RestController
@RequestMapping("/me")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping
    public Result<Void> postVideo(@ModelAttribute PostVideoRequest postVideoRequest) throws IOException, InterruptedException {
        videoService.postVideo(UserContext.getUser(), postVideoRequest);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<VideoMainWithInteractionResponse>> getVideosWithInteractionStatus() {
        return Result.success(videoService.getVideosWithInteractionStatus(UserContext.getUser()));
    }
}

