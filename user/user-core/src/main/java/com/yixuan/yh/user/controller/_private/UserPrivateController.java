package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private")
public class UserPrivateController {

    @Autowired
    private UserService userService;

    @GetMapping("/name")
    public Result<String> getName(@RequestParam String id) {
        return Result.success(userService.getName(id));
    }

    @GetMapping("/info-in-list")
    public Result<List<UserInfoInListResponse>> getUserInfoInList(@RequestParam List<Long> idList) {
        return Result.success(userService.getUserInfoInList(idList));
    }
}
