package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.commom.response.Result;
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

        String url = "http://127.0.0.1:10100/check_duplicate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VideoPostMessage> request = new HttpEntity<>(videoPostMessage, headers);

        ResponseEntity<Result> response = restTemplate.postForEntity(url, request, Result.class);
        response.getBody();
        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Video “check duplicate” service error!");
        }
    }
}
