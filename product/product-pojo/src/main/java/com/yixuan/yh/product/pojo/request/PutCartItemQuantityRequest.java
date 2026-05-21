package com.yixuan.yh.product.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutCartItemQuantityRequest {
    private Integer quantity;
}
