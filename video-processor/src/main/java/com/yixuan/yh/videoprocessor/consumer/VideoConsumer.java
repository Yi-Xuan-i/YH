package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.videoprocessor.mq.VideoPostMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VideoConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(queues = "video.post.queue")
    public void handleVideoPostMessage(VideoPostMessage videoPostMessage) {

        /* 查重与特征保存 */
        // 依靠主键唯一性确保幂等
        String url = "http://127.0.0.1:10100/check_duplicate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VideoPostMessage> request = new HttpEntity<>(videoPostMessage, headers);

        ResponseEntity<Result> response = restTemplate.postForEntity(url, request, Result.class);
        Result<Long> result = response.getBody();
        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Video “check duplicate” service error!");
        }

        /* 查重成功，则代表视频要变成“上架” */
        if (result.getData() == null) {

        }

    }
}
