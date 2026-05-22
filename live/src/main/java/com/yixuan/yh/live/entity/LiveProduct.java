package com.yixuan.yh.live.entity;

import lombok.Data;

@Data
public class LiveProduct {
    Long id;
    Long roomId;
    Long productId;
    String title;
    String imageUrl;
    Integer salesVolume;
}
