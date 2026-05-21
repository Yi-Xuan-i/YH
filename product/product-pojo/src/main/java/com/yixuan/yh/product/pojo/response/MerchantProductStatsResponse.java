package com.yixuan.yh.product.pojo.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantProductStatsResponse {
    private Integer productCount;
    private Integer dailySalesVolume;
    private BigDecimal dailySalesAmount;
}
