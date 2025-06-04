package com.yixuan.yh.product.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItem {
    private Long cartItemId;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private BigDecimal price;
    private String selectedSku;
    private Date createdTime;
}

