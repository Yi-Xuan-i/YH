package com.yixuan.study.microservices.user.response;

import com.yixuan.study.microservices.user.entity.Merchant;
import lombok.Data;

@Data
public class MerchantBasicDataResponse {
    private String name;
    private String contactPhone;
    private String avatarUrl;
    private Merchant.CertificationStatus certificationStatus;
}
