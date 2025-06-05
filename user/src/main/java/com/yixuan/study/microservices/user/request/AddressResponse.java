package com.yixuan.study.microservices.user.request;

import lombok.Data;

@Data
public class AddressResponse {
    private Long addressId;

    private Boolean isDefault;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;
}
