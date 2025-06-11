package com.yixuan.yh.live.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class PostLiveProductRequest {
    Long roomId;
    String name;
    BigDecimal price;
    Integer stock;
    MultipartFile image;
}
