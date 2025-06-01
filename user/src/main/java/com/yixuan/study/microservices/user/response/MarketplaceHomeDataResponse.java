package com.yixuan.study.microservices.user.response;

import lombok.Data;

@Data
public class MarketplaceHomeDataResponse {
    Integer cartItemCount;
    Boolean isMerchant;
}
