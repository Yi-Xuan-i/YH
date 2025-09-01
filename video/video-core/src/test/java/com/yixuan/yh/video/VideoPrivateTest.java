package com.yixuan.yh.video;

import com.yixuan.yh.video.controller._private.VideoPrivateController;
import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.pojo.entity.Video;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class VideoPrivateTest {

    @Autowired
    private VideoPrivateController videoPrivateController;
    @Autowired
    private VideoMapper videoMapper;

    @Test
    public void putVideoStatusToPublished() {
        Long videoId = videoMapper.selectFirst().getId();
        videoPrivateController.putVideoStatusToPublished(videoId);
        Video.VideoStatus status = videoMapper.selectVideoStatusUrlById(videoId);
        assertEquals(Video.VideoStatus.PUBLISHED, status, "修改 Video Status 失败！");
    }


}
