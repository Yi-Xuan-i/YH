package com.yixuan.yh.admin.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String name;
    private LocalDateTime createdTime;
}
