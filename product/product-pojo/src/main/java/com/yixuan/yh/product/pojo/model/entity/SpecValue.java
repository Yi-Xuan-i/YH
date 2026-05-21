package com.yixuan.yh.product.pojo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecValue {
    private Long valueId;
    private String valueName;
    private String imageUrl;
}
