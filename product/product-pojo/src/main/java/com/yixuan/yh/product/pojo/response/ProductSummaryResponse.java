package com.yixuan.yh.product.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSummaryResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;
    private String title;
    private String coverUrl;
    private BigDecimal price;
    private Integer salesVolume;
}
