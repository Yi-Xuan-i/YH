package com.yixuan.yh.order.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("order_item")
@Data
public class OrderItem {
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Long skuId;
    private String sku;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime payTime;
}