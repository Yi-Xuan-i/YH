package com.yixuan.yh.video.controller;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.video.response.GetSimpleVideoTagResponse;
import com.yixuan.yh.video.service.VideoTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Video-Tag")
@RestController
@RequestMapping("/tag")
public class VideoTagController {

    @Autowired
    private VideoTagService videoTagService;

    @GetMapping("/simple")
    public Result<List<GetSimpleVideoTagResponse>> getSimpleVideoTags() {
        return Result.success(videoTagService.getSimpleVideoTags());
    }
}
