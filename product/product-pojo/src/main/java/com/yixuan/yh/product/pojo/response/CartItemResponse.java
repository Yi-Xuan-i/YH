package com.yixuan.yh.product.pojo.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    Long cartItemId;
    Long merchantId;
    String merchantName;
    Long productId;
    String productTitle;
    String coverUrl;
    Integer quantity;
    Integer stock;
    BigDecimal price;
    BigDecimal currentPrice;
    String selectedSku;
}
