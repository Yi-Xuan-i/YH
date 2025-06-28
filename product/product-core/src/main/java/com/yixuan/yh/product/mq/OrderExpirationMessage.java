package com.yixuan.yh.product.mq;

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
