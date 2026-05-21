package com.yixuan.yh.order.pojo.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantDailySalesResponse {
    private Integer dailySalesVolume;
    private BigDecimal dailySalesAmount;
}
