package com.yixuan.yh.product.pojo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("cart_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @TableId(type = IdType.ASSIGN_ID)
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

