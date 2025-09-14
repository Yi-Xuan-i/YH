package com.yixuan.yh.video.controller._public;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Tag(name = "Video")
@RestController
@RequestMapping("/public")
public class VideoPublicController {

    @Autowired
    private VideoService videoService;

    @Operation(summary = "获取视频（未登录）")
    @GetMapping("/list")
    public Result<List<VideoMainResponse>> getVideos() {
        return Result.success(videoService.getVideos());
    }
}
