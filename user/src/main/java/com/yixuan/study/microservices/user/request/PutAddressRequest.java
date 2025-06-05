package com.yixuan.study.microservices.user.request;

import lombok.Data;

@Data
public class PutAddressRequest {
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Boolean isDefault;
}