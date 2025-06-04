package com.yixuan.yh.product.pojo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCarouselRequest {
    private MultipartFile carouselFile;
}
