package com.yixuan.yh.product.pojo.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartOfOrderResponse {
    Long merchantId;
    String productName;
    BigDecimal price;
}
