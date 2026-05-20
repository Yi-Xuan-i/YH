package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.user.mapper.UserMapper;
import com.yixuan.yh.user.mapstruct.UserMapStruct;
import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.user.pojo.response.UserSearchResponse;
import com.yixuan.yh.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AWSUtils awsUtils;


    @Override
    public String getName(String id) {
        return userMapper.selectNameById(id);
    }

    @Override
    public Map<Long, String> getNameBatch(List<Long> idList) {
        return userMapper.selectNameByIdList(idList)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));
    }

    @Override
    public List<UserInfoInListResponse> getUserInfoInList(List<Long> idList) {
        List<User> userList = userMapper.selectUserInfoInList(idList);

        return userList.stream().map(u -> {
            UserInfoInListResponse response = UserMapStruct.INSTANCE.toUserInfoInListResponse(u);
            response.setAvatarUrl(awsUtils.generateAccessUrl(u.getAvatarUrl()));
            return response;
        }).toList();
    }

    @Override
    public List<UserSearchResponse> search(Long userId, String query) {
        return userMapper.selectUserByNamePrefix(userId, query);
    }
}
