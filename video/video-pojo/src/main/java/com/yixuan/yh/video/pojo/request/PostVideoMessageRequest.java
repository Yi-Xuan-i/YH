package com.yixuan.yh.video.pojo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostVideoMessageRequest {
    String description;
    List<Long> addedTagList;
    List<String> addedNewTagList;
    MultipartFile cover;
    MultipartFile video;
}
