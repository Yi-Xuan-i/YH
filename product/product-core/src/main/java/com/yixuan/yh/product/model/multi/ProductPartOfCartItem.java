package com.yixuan.yh.product.model.multi;

import lombok.Data;

@Data
public class ProductPartOfCartItem {
    Long productId;
    Long merchantId;
    String merchantName;
    String title;
    String coverUrl;
}
