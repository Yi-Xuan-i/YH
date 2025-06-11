package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.user.mapper.UserMapper;
import com.yixuan.yh.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String getName(String id) {
        return userMapper.selectNameById(id);
    }
}
