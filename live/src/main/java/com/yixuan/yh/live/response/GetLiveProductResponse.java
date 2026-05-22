package com.yixuan.yh.live.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLiveProductResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
}
