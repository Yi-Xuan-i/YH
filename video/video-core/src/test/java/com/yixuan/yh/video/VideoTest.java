package com.yixuan.yh.video;

import com.yixuan.yh.video.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class VideoTest {

    @Autowired
    private VideoService videoService;

    @Test
    public void getUploadedVideo() {
        Long userId = 13975121182064640L;
        System.out.println(videoService.getUploadedVideo(userId));
    }

    @Test
    public void getPublishedVideo() {
        Long userId = 13980400355377152L;
        System.out.println(videoService.getUploadedVideo(userId));
    }



}