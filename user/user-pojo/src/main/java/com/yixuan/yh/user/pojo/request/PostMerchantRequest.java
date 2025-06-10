package com.yixuan.yh.user.pojo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostMerchantRequest {
    private String name;
    private String contactPhone;
    private MultipartFile avatar;
}
