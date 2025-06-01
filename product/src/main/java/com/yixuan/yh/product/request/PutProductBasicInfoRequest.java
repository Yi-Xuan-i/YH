package com.yixuan.yh.product.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class PutProductBasicInfoRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private MultipartFile cover;
}
