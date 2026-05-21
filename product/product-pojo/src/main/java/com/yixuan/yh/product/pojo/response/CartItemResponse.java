package com.yixuan.yh.product.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cartItemId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private String merchantName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;
    private String productTitle;
    private String coverUrl;
    private Integer quantity;
    private Integer stock;
    private BigDecimal price;
    private BigDecimal currentPrice;
    private String selectedSku;
}
