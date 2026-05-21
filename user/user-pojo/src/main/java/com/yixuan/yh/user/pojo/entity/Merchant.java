package com.yixuan.yh.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("merchant")
@Data
public class Merchant {
    @TableId(type = IdType.ASSIGN_ID)
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