package com.yixuan.yh.product.pojo.response;

import com.yixuan.yh.product.pojo.model.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductManageItemResponse {
    private Long productId;
    private String title;
    private BigDecimal price;
    private Integer stock;
    private Product.ProductStatus status; // 枚举类型
    private Integer salesVolume;
    private Float rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
