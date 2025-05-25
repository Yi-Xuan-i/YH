package com.yixuan.yh.video.service;

import com.yixuan.yh.video.request.PostVideoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    List<String> getVideos();

    void postVideo(PostVideoRequest postVideoRequest) throws IOException, InterruptedException;
}
