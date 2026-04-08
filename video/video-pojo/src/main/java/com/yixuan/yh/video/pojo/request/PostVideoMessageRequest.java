package com.yixuan.yh.video.pojo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostVideoMessageRequest {
    Long videoId;
    String description;
    List<String> addedTagList;
    MultipartFile cover;
}
