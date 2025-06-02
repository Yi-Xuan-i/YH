package com.yixuan.yh.product.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostCarouseRequest {
    private MultipartFile carouselFile;
}
