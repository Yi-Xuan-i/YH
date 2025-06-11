package com.yixuan.yh.live.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiveProduct {
    Long id;
    Long roomId;
    String name;
    BigDecimal price;
    Integer stock;
    String imageUrl;
}
