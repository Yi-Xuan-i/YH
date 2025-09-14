package com.yixuan.yh.live.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.live.request.StartLiveRequest;
import com.yixuan.yh.live.service.LiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Live")
@RestController
@RequestMapping("/me")
public class LiveController {

    @Autowired
    LiveService liveService;

    @Operation(summary = "请求开始直播")
    @PostMapping("/start")
    public Result<String> postStartLive(@ModelAttribute StartLiveRequest startLiveRequest) throws IOException {
        return Result.success(liveService.postStartLive(UserContext.getUser(), startLiveRequest));
    }

}
