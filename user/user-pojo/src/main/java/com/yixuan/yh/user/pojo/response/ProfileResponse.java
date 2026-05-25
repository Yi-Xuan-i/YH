package com.yixuan.yh.user.pojo.response;

import com.yixuan.yh.user.pojo._enum.FollowStatus;
import lombok.Data;

@Data
public class ProfileResponse {
    private String avatarUrl;
    private String name;
    private String bio;
    private FollowStatus followStatus;
}
