package com.yixuan.yh.video.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Long id;
    private String phoneNumber;
    private String encodedPassword;
    private LocalDateTime createdTime;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}
