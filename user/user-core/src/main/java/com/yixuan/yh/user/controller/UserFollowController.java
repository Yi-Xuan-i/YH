package com.yixuan.yh.user.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.service.UserFollowService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/follow")
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @PostMapping("/{followeeId}")
    public Result<Void> follow(@PathVariable Long followeeId) throws BadRequestException {
        userFollowService.follow(UserContext.getUser(), followeeId);
        return Result.success();
    }

    @DeleteMapping("/{followeeId}")
    public Result<Void> unfollow(@PathVariable Long followeeId) {
        userFollowService.unfollow(UserContext.getUser(), followeeId);
        return Result.success();
    }

}
