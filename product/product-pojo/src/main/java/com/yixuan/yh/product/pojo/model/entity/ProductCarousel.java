package com.yixuan.yh.product.pojo.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCarousel {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String url;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;
}
