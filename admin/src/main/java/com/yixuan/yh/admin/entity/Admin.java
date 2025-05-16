package com.yixuan.yh.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {
    private Long id;
    private String name;
    private String encodedPassword;
    private LocalDateTime createdTime;

    public void setName(String name) {
        this.name = name;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}
