package com.yixuan.yh.order.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostOrderResponse {
    Long orderId;
    String payUrl;
}
