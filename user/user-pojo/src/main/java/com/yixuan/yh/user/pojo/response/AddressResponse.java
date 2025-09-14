package com.yixuan.yh.user.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class AddressResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long addressId;

    private Boolean isDefault;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;
}
