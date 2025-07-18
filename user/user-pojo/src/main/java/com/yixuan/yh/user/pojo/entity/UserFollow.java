package com.yixuan.yh.user.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFollow {
    Long id;
    Long followerId;
    Long followeeId;
    LocalDateTime createdTime;
}
