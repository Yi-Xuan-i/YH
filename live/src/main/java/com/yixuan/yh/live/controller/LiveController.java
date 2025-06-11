package com.yixuan.yh.live.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.live.request.StartLiveRequest;
import com.yixuan.yh.live.service.LiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class LiveController {

    @Autowired
    LiveService liveService;

    @PostMapping("/start")
    public Result<Long> postStartLive(@RequestBody StartLiveRequest startLiveRequest) {
        return Result.success(liveService.postStartLive(UserContext.getUser(), startLiveRequest));
    }

}
