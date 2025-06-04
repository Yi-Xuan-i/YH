package com.yixuan.yh.product.pojo.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostCartItemRequest {
    Long productId;
    Long skuId;
    Integer quantity;
    BigDecimal price;
    String selectedSku;
}
