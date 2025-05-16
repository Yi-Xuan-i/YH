package com.yixuan.study.microservices.user.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.study.microservices.user.entity.User;
import com.yixuan.study.microservices.user.mapper.UserMapper;
import com.yixuan.study.microservices.user.mapstruct.ProfileMapStruct;
import com.yixuan.study.microservices.user.request.ProfileRequest;
import com.yixuan.study.microservices.user.response.BasicProfileResponse;
import com.yixuan.study.microservices.user.response.ProfileResponse;
import com.yixuan.study.microservices.user.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MTClient mtClient;

    @Override
    public ProfileResponse getProfile(Long userId) {
        return userMapper.selectProfile(userId);
    }

    @Override
    public BasicProfileResponse getBasicProfile(Long userId) {
        User user = userMapper.selectBasicProfile(userId);
        return ProfileMapStruct.INSTANCE.userToBasicProfileResponse(user);
    }

    @Override
    public String postAvatar(Long userId, MultipartFile avatar) throws IOException {
        String newAvatarUrl = mtClient.upload(avatar);
        userMapper.updateAvatarUrl(userId, newAvatarUrl);

        return newAvatarUrl;
    }

    @Override
    public void putProfile(Long userId, ProfileRequest profileRequest) {
        userMapper.update(userId, profileRequest);
    }
}
