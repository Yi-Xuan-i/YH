package com.yixuan.study.microservices.user.service;

import com.yixuan.study.microservices.user.request.ProfileRequest;
import com.yixuan.study.microservices.user.response.BasicProfileResponse;
import com.yixuan.study.microservices.user.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileResponse getProfile(Long userId);

    BasicProfileResponse getBasicProfile(Long userId);

    String postAvatar(Long userId, MultipartFile avatar) throws IOException;

    void putProfile(Long userId, ProfileRequest profileRequest);
}
