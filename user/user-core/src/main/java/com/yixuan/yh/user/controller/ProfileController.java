package com.yixuan.yh.user.controller;

import com.yixuan.yh.user.pojo.request.ProfileRequest;
import com.yixuan.yh.user.pojo.response.BasicProfileResponse;
import com.yixuan.yh.user.pojo.response.ProfileResponse;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Profile")
@RestController
@RequestMapping("/me/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "获取个人简介")
    @GetMapping
    public Result<ProfileResponse> getProfile() {
        return Result.success(profileService.getProfile(UserContext.getUser()));
    }

    @Operation(summary = "获取个人基本信息（主要用于主页渲染）")
    @GetMapping("/basic")
    public Result<BasicProfileResponse> getBasicProfile() {
        return Result.success(profileService.getBasicProfile(UserContext.getUser()));
    }

    @Operation(summary = "上传（修改）头像")
    @PostMapping("/avatar")
    public Result<String> postAvatar(MultipartFile avatar) throws IOException {
        return Result.success(profileService.postAvatar(UserContext.getUser(), avatar));
    }

    @Operation(summary = "随机修改头像")
    @PutMapping("/random-avatar")
    public Result<String> randomAvatar() throws IOException {
        return Result.success(profileService.randomAvatar(UserContext.getUser()));
    }

    @Operation(summary = "修改个人简介")
    @PutMapping
    public Result<Void> putProfile(@RequestBody ProfileRequest profileRequest) {
        profileService.putProfile(UserContext.getUser(), profileRequest);
        return Result.success();
    }
}
