package com.yixuan.yh.user.service;

import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserFollowService {
    void follow(Long followerId, Long followeeId) throws BadRequestException;

    void unfollow(Long followerId, Long followeeId);

    List<Boolean> getFollowStatus(Long followerId, List<Long> followeeIdList);
}
