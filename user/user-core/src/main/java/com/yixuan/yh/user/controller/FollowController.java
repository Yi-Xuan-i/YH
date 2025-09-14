package com.yixuan.yh.user.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.pojo.response.UserFriendResponse;
import com.yixuan.yh.user.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "/Follow")
@RestController
@RequestMapping("/me/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Operation(summary = "关注")
    @PostMapping("/{followeeId}")
    public Result<Void> follow(@PathVariable Long followeeId) throws BadRequestException {
        followService.follow(UserContext.getUser(), followeeId);
        return Result.success();
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/{followeeId}")
    public Result<Void> unfollow(@PathVariable Long followeeId) {
        followService.unfollow(UserContext.getUser(), followeeId);
        return Result.success();
    }

    @Operation(summary = "获取好友（互关）")
    @GetMapping("/friends")
    public Result<List<UserFriendResponse>> getFriends() {
       return Result.success(followService.getFriends(UserContext.getUser()));
    }

}
