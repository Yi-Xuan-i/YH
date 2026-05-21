package com.yixuan.yh.product.pojo.model.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;

@TableName("product")
@Data
public class Product {
    @TableId(type = IdType.ASSIGN_ID)
    private Long productId;
    private Long merchantId;
    private Integer categoryId;
    private Long defaultSkuId;
    private String title;
    private String coverUrl;
    private String description;
    private ProductStatus status; // 枚举类型
    private Boolean isHot;
    private Integer salesVolume;
    private Float rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ProductStatus {
        PENDING(1, "待审核"),
        APPROVED(2, "审核通过"),
        REJECTED(3, "审核驳回"),
        ON_SALE(4, "上架销售"),
        OFF_SHELF(5, "下架内测");

        @EnumValue
        private final int code;
        private final String desc;

        ProductStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        @JsonValue
        public int getCode() {
            return code;
        }

        public static ProductStatus getByCode(int code) {
            return Arrays.stream(ProductStatus.values())
                    .filter(status -> status.getCode() == code)
                    .findFirst()
                    .orElse(null);
        }
    }
}