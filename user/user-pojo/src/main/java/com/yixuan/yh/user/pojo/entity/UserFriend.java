package com.yixuan.yh.user.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFriend {
    Long id;
    Long userId;
    Long friendId;
    LocalDateTime createdTime;
}
