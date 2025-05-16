package com.yixuan.study.microservices.user.entity;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phoneNumber;
    private String name;
    private String encodedPassword;
    private String avatarUrl;
    private LocalDateTime createdTime;
}
