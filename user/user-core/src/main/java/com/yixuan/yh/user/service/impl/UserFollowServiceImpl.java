package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.mapper.UserFollowMapper;
import com.yixuan.yh.user.mapper.UserFriendMapper;
import com.yixuan.yh.user.pojo.entity.UserFollow;
import com.yixuan.yh.user.pojo.entity.UserFriend;
import com.yixuan.yh.user.service.UserFollowService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserFollowServiceImpl implements UserFollowService {

    @Autowired
    private UserFollowMapper userFollowMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private UserFriendMapper userFriendMapper;

    @Override
    @Transactional
    public void follow(Long followerId, Long followeeId) throws BadRequestException {
        // 不能关注自己
        if (followerId.equals(followeeId)) {
            throw new BadRequestException("不能关注自己！");
        }

        UserFollow userFollow = new UserFollow();
        userFollow.setId(snowflakeUtils.nextId());
        userFollow.setFollowerId(followerId);
        userFollow.setFolloweeId(followeeId);
        userFollow.setCreatedTime(LocalDateTime.now());

        // 如果已经插入则忽略
        if (!userFollowMapper.insertIgnore(userFollow)) {
            return;
        }

        // 查询是否有反向关系
        if (!userFollowMapper.selectIsRelationExist(followeeId, followerId)) {
            return;
        }

        // 插入好友记录
        UserFriend userFriend = new UserFriend();
        userFriend.setId(snowflakeUtils.nextId());
        userFriend.setUserId(followerId);
        userFriend.setFriendId(followeeId);
        userFriend.setCreatedTime(LocalDateTime.now());
        userFriendMapper.insertEach(userFriend);

//        // 保存异步消息
//        UserNewFriendMessage userNewFriendMessage = new UserNewFriendMessage();
//        userNewFriendMessage.setId(snowflakeUtils.nextId());
//        userNewFriendMessage.setUserId(followerId);
//        userNewFriendMessage.setFriendId(followeeId);
//
//        userNewFriendMessageMapper.insert(userNewFriendMessage);
    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        // 尝试删除关注记录
        if (!userFollowMapper.deleteByFollowerIdAndFolloweeId(followerId, followeeId)) {
            return;
        }

        // 查询是否有反向关系
        if (!userFollowMapper.selectIsRelationExist(followeeId, followerId)) {
            return;
        }

        // 有反向关系，删除好友记录
        userFriendMapper.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public List<Boolean> getFollowStatus(Long followerId, List<Long> followeeIdList) {
        return userFollowMapper.selectFollowStatusBatch(followerId, followeeIdList);
    }


}
