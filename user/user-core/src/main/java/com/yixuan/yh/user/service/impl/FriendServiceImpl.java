package com.yixuan.yh.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.user.mapper.FriendMapper;
import com.yixuan.yh.user.pojo.entity.UserFriend;
import com.yixuan.yh.user.service.FriendService;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, UserFriend> implements FriendService {
}
