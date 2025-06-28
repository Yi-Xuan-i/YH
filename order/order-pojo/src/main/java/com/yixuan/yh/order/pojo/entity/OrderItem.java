package com.yixuan.yh.order.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Long skuId;
    private String sku;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}