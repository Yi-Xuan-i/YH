package com.yixuan.yh.user.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.pojo.response.UserFriendResponse;
import com.yixuan.yh.user.service.FollowService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{followeeId}")
    public Result<Void> follow(@PathVariable Long followeeId) throws BadRequestException {
        followService.follow(UserContext.getUser(), followeeId);
        return Result.success();
    }

    @DeleteMapping("/{followeeId}")
    public Result<Void> unfollow(@PathVariable Long followeeId) {
        followService.unfollow(UserContext.getUser(), followeeId);
        return Result.success();
    }

    @GetMapping("/friends")
    public Result<List<UserFriendResponse>> getFriends() {
       return Result.success(followService.getFriends(UserContext.getUser()));
    }

}
