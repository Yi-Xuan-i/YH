package com.yixuan.study.microservices.user.controller;

import com.yixuan.study.microservices.user.request.ProfileRequest;
import com.yixuan.study.microservices.user.response.BasicProfileResponse;
import com.yixuan.study.microservices.user.response.ProfileResponse;
import com.yixuan.study.microservices.user.service.ProfileService;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
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

    @PutMapping
    public Result<Void> putProfile(@RequestBody ProfileRequest profileRequest) {
        profileService.putProfile(UserContext.getUser(), profileRequest);
        return Result.success();
    }
}
