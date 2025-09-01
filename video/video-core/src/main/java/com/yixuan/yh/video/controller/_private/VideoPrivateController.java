package com.yixuan.yh.video.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.service.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "VideoPrivate")
@RestController
@RequestMapping("/private")
public class VideoPrivateController {

    @Autowired
    private VideoService videoService;

    @PutMapping("/to-published/{videoId}")
    public Result<Void> putVideoStatusToPublished(@PathVariable Long videoId) {
        videoService.putVideoStatusToPublished(videoId);
        return Result.success();
    }

}
