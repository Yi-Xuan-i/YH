package com.yixuan.yh.product.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOnSaleProductForLiveResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;
    private String title;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
