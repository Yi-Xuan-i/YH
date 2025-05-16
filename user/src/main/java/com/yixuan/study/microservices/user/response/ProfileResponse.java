package com.yixuan.study.microservices.user.response;

import lombok.Data;

@Data
public class ProfileResponse {
    String avatarUrl;
    String name;
    String bio;
}
