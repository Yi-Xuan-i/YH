package com.yixuan.yh.product.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProductSku {
    private Long skuId;
    private Long productId;
    private String spec;
    private BigDecimal price;
    private Integer stock;
    private String skuThumb;
}