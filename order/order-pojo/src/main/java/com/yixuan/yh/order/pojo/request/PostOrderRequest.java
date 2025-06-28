package com.yixuan.yh.order.pojo.request;

import lombok.Data;

@Data
public class PostOrderRequest {
    Long productId;
    Long skuId;
    String sku;
    String deliveryAddress;
    Integer quantity;
}
