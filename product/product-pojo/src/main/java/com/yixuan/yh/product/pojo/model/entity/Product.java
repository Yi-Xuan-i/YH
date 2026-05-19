package com.yixuan.yh.product.pojo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product")
@Data
public class Product {
    @TableId(type = IdType.ASSIGN_ID)
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

    @Getter
    @AllArgsConstructor
    public enum ProductStatus {
        PENDING(1, "待审核"),
        APPROVED(2, "审核通过"),
        REJECTED(3, "审核驳回"),
        ON_SALE(4, "上架销售"),
        OFF_SHELF(5, "下架内测");

        private final int code;
        private final String desc;
    }
}