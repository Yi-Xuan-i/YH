package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/private/follow")
public class FollowPrivateController {

    @Autowired
    private FollowService followService;

    @GetMapping("/status")
    public Result<List<Boolean>> getFollowStatus(@RequestParam Long followerId, @RequestParam List<Long> followeeIdList) {
        return Result.success(followService.getFollowStatus(followerId, followeeIdList));
    }
}
