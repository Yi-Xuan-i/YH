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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

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

    @Override
    public String randomAvatar(Long userId) throws IOException {
        String url = "http://127.0.0.1:10011/generate-image-multipart";

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        byte[] fileContent = response.getBody();
        String filename = extractFilename(response.getHeaders());

        MultipartFile multipartFile = createMultipartFile(filename, fileContent);

        String newAvatarUrl = mtClient.upload(multipartFile);

        userMapper.updateAvatarUrl(userId, newAvatarUrl);

        return newAvatarUrl;
    }

    // 从响应头中提取文件名
    private static String extractFilename(HttpHeaders headers) {
        String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            return contentDisposition.split("filename=")[1].replace("\"", "");
        }
        return "generated_image.png"; // 默认文件名
    }

    // 创建 MultipartFile 对象
    private static MultipartFile createMultipartFile(String filename, byte[] content) {
        return new MockMultipartFile(
                "file",       // 表单字段名
                filename,           // 原始文件名
                "image/png",        // 内容类型
                content             // 文件内容
        );
    }
}
