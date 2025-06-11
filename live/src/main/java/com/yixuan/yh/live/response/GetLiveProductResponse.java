package com.yixuan.yh.live.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetLiveProductResponse {
    Long id;
    String name;
    BigDecimal price;
    Integer stock;
    String imageUrl;
}
