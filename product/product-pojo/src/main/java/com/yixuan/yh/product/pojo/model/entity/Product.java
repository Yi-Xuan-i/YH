package com.yixuan.yh.product.pojo.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long productId;
    private Long merchantId;
    private Integer categoryId;
    private String title;
    private String coverUrl;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status; // 枚举类型
    private Boolean isHot;
    private Integer salesVolume;
    private Float rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 枚举定义
    public enum ProductStatus {
        PENDING, APPROVED, REJECTED, ON_SALE, OFF_SHELF
    }
}