package com.yixuan.yh.order.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderExpirationMessage {
    Long orderId;
    Long skuId;
    Integer quantity;
}
