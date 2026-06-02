package com.yixuan.yh.order.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("`order`")
@Data
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private BigDecimal paymentAmount;
    private OrderStatus orderStatus;
    private String deliveryAddress;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public enum OrderStatus {
        UNPAID,
        PAID,
        SHIPPED,
        COMPLETED,
        CANCELLED,
        REFUND_PROCESSING,
        REFUNDED
    }
}
