package com.yixuan.study.microservices.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Merchant {
    private Long merchantId;
    private String name;
    private String contactPhone;
    private String avatarUrl;
    private CertificationStatus certificationStatus;
    private LocalDateTime createdAt;

    // 认证状态枚举
    public enum CertificationStatus {
        UNCERTIFIED, PENDING, CERTIFIED
    }
}