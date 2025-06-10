package com.yixuan.yh.user.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.user.mapper.UserMapper;
import com.yixuan.yh.user.mapstruct.ProfileMapStruct;
import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.request.ProfileRequest;
import com.yixuan.yh.user.pojo.response.BasicProfileResponse;
import com.yixuan.yh.user.pojo.response.ProfileResponse;
import com.yixuan.yh.user.service.ProfileService;
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
