package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "UserPrivate")
@RestController
@RequestMapping("/private")
public class UserPrivateController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户名")
    @GetMapping("/name")
    public Result<String> getName(@RequestParam String id) {
        return Result.success(userService.getName(id));
    }

    @Operation(summary = "批量获取用户名")
    @GetMapping("/name/batch")
    Result<Map<Long, String>> getNameBatch(@RequestParam List<Long> idList) {
        return Result.success(userService.getNameBatch(idList));
    }

    @Operation(summary = "获取用户信息（一般用于展示用户列表）")
    @GetMapping("/info-in-list")
    public Result<List<UserInfoInListResponse>> getUserInfoInList(@RequestParam List<Long> idList) {
        return Result.success(userService.getUserInfoInList(idList));
    }
}
