package com.yixuan.yh.video.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    List<String> getVideos();

    String postVideo(MultipartFile videoFile) throws IOException;
}
