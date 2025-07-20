package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.response.UserFriendResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface FollowService {
    void follow(Long followerId, Long followeeId) throws BadRequestException;

    void unfollow(Long followerId, Long followeeId);

    List<Boolean> getFollowStatus(Long followerId, List<Long> followeeIdList);

    List<UserFriendResponse> getFriends(Long user);
}
