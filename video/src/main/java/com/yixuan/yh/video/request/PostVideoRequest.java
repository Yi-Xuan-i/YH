package com.yixuan.yh.video.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostVideoRequest {
    String description;
    List<Long> addedTagList;
    List<String> addedNewTagList;
    MultipartFile cover;
    MultipartFile video;
}
