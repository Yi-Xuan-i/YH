package com.yixuan.yh.order.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("`order`")
@Data
public class Order {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private BigDecimal paymentAmount;
    private OrderStatus orderStatus;
    private String deliveryAddress;
    private LocalDateTime createdTime;

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
