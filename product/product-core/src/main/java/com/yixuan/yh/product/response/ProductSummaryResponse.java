package com.yixuan.yh.product.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSummaryResponse {
    private Long productId;
    private String title;
    private String coverUrl;
    private BigDecimal price;
    private Integer salesVolume;
}
