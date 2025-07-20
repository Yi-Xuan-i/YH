package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.user.mapper.UserMapper;
import com.yixuan.yh.user.mapstruct.UserMapStruct;
import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String getName(String id) {
        return userMapper.selectNameById(id);
    }

    @Override
    public List<UserInfoInListResponse> getUserInfoInList(List<Long> idList) {
        List<User> userList = userMapper.selectUserInfoInList(idList);

        return userList.stream().map(UserMapStruct.INSTANCE::toUserInfoInListResponse).toList();
    }
}
