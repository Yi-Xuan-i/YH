package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.request.ProfileRequest;
import com.yixuan.yh.user.pojo.response.ProfileBasicResponse;
import com.yixuan.yh.user.pojo.response.ProfileResponse;
import com.yixuan.yh.user.pojo.response.ProfileStatsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileResponse getProfile(Long userId);

    ProfileBasicResponse getProfileBasic(Long userId);

    ProfileStatsResponse getProfileStats(Long user);

    String postAvatar(Long userId, MultipartFile avatar) throws IOException;

    void putProfile(Long userId, ProfileRequest profileRequest);

    String randomAvatar(Long userId) throws IOException;
}
