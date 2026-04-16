package com.yixuan.yh.product.pojo.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long cartItemId;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private BigDecimal price;
    private String selectedSku;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}

