package com.yixuan.yh.video.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.service.VideoTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Video-Tag")
@RestController
@RequestMapping("/private/tag")
public class VideoTagPrivateController {

    @Autowired
    private VideoTagService videoTagService;

    @Operation(summary = "获取视频拥有的标签")
    @GetMapping("/{videoId}")
    public Result<List<Long>> getVideoTags(@PathVariable Long videoId) {
        return Result.success(videoTagService.getVideoTags(videoId));
    }
}
