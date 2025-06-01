package com.yixuan.yh.video.controller;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.video.request.PostVideoRequest;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Video")
@RestController
@RequestMapping("/me")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping
    public Result<Void> postVideo(@ModelAttribute PostVideoRequest postVideoRequest) throws IOException, InterruptedException {
        videoService.postVideo(postVideoRequest);
        return Result.success();
    }
}

