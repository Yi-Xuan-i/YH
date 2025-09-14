package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Follow")
@RestController
@RequestMapping("/private/follow")
public class FollowPrivateController {

    @Autowired
    private FollowService followService;

    @Operation(summary = "获取关注状态")
    @GetMapping("/status")
    public Result<List<Boolean>> getFollowStatus(@RequestParam Long followerId, @RequestParam List<Long> followeeIdList) {
        return Result.success(followService.getFollowStatus(followerId, followeeIdList));
    }
}
