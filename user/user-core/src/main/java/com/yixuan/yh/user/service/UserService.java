package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;

import java.util.List;

public interface UserService {
    String getName(String id);

    List<UserInfoInListResponse> getUserInfoInList(List<Long> idList);
}
