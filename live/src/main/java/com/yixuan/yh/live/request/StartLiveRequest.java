package com.yixuan.yh.live.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StartLiveRequest {
    String title;
    MultipartFile coverFile;
}
