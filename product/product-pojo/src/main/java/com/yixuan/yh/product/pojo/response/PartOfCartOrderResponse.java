package com.yixuan.yh.product.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartOfCartOrderResponse {
    private Long merchantId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Long productId;
    private Long skuId;
    private String sku;
}
