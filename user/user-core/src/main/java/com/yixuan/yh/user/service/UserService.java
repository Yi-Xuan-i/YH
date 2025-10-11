package com.yixuan.yh.user.service;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import com.yixuan.yh.user.pojo.response.UserSearchResponse;

import java.util.List;

public interface UserService {
    String getName(String id);

    List<UserInfoInListResponse> getUserInfoInList(List<Long> idList);

    List<UserSearchResponse> search(Long userId, String query);
}
