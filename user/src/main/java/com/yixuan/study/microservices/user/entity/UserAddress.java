package com.yixuan.study.microservices.user.entity;

import lombok.Data;

@Data
public class UserAddress {
    private Long addressId;

    private Long userId;

    private Boolean isDefault;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;
}
