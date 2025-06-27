package com.yixuan.yh.user.controller;

import com.yixuan.yh.user.pojo.request.ProfileRequest;
import com.yixuan.yh.user.pojo.response.BasicProfileResponse;
import com.yixuan.yh.user.pojo.response.ProfileResponse;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/me/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public Result<ProfileResponse> getProfile() {
        return Result.success(profileService.getProfile(UserContext.getUser()));
    }

    @GetMapping("/basic")
    public Result<BasicProfileResponse> getBasicProfile() {
        return Result.success(profileService.getBasicProfile(UserContext.getUser()));
    }

    @PostMapping("/avatar")
    public Result<String> postAvatar(MultipartFile avatar) throws IOException {
        return Result.success(profileService.postAvatar(UserContext.getUser(), avatar));
    }

    @PutMapping("/random-avatar")
    public Result<String> randomAvatar() throws IOException {
        return Result.success(profileService.randomAvatar(UserContext.getUser()));
    }

    @PutMapping
    public Result<Void> putProfile(@RequestBody ProfileRequest profileRequest) {
        profileService.putProfile(UserContext.getUser(), profileRequest);
        return Result.success();
    }
}
