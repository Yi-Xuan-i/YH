package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.feign.InteractionPrivateClient;
import com.yixuan.yh.video.feign.VideoPrivateClient;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.videoprocessor.mq.VideoInteractionMessage;
import com.yixuan.yh.videoprocessor.mq.VideoPostMessage;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class


VideoConsumer {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private VideoPrivateClient videoPrivateClient;
    @Autowired
    private InteractionPrivateClient interactionPrivateClient;

    @RabbitListener(queuesToDeclare = @Queue(name = "video.post.queue"))
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
        if (result.isError()) {
            Result<Void> putVideoStatusResult = videoPrivateClient.putVideoStatusToPublished(videoPostMessage.getVideoId());
            if (putVideoStatusResult.isError()) {
                throw new RuntimeException("Video “check duplicate” service error!");
            }
        }

    }

    private VideoInteractionBatchRequest createInteractionBatchRequest(List<VideoInteractionMessage> videoInteractionMessageList) {
        // 计算视频需要增加或减少的点赞数
        Map<Long, Long> videoInteractionNumberMap = new HashMap<>();
        for (VideoInteractionMessage videoInteractionMessage : videoInteractionMessageList) {
            if (!videoInteractionNumberMap.containsKey(videoInteractionMessage.getVideoId())) {
                videoInteractionNumberMap.put(videoInteractionMessage.getVideoId(), 0L);
            }
            videoInteractionNumberMap.replace(videoInteractionMessage.getVideoId(), videoInteractionNumberMap.get(videoInteractionMessage.getVideoId()) + videoInteractionMessage.getStatus().getValue());
        }
        // 构建点赞数对象
        List<VideoInteractionBatchRequest.Incr> incrList = new ArrayList<>(videoInteractionMessageList.size());
        for (Map.Entry<Long, Long> entry : videoInteractionNumberMap.entrySet()) {
            VideoInteractionBatchRequest.Incr incr = new VideoInteractionBatchRequest.Incr();
            incr.setVideoId(entry.getKey());
            incr.setIncrNumber(entry.getValue());

            incrList.add(incr);
        }
        // 构建点赞记录对象
        List<VideoInteractionBatchRequest.Record> recordList = new ArrayList<>(videoInteractionMessageList.size());
        for (VideoInteractionMessage videoInteractionMessage : videoInteractionMessageList) {
            VideoInteractionBatchRequest.Record record = new VideoInteractionBatchRequest.Record();
            record.setVideoId(videoInteractionMessage.getVideoId());
            record.setUserId(videoInteractionMessage.getUserId());
            record.setStatus(videoInteractionMessage.getStatus().getValue() == 1 ? VideoInteractionBatchRequest.Record.Status.LIKE : VideoInteractionBatchRequest.Record.Status.UNLIKE);

            recordList.add(record);
        }
        // 构建完整对象
        VideoInteractionBatchRequest videoInteractionBatchRequest = new VideoInteractionBatchRequest();
        videoInteractionBatchRequest.setInteractionIncrList(incrList);
        videoInteractionBatchRequest.setInteractionRecordList(recordList);

        return videoInteractionBatchRequest;
    }

    @RabbitListener(queuesToDeclare = @Queue(name = "video.like.queue"), containerFactory = "batchContainerFactory")
    public void handleVideoLikeMessage(List<VideoInteractionMessage> videoInteractionMessageList) {
        // 发起请求
        if (interactionPrivateClient.likeBatch(createInteractionBatchRequest(videoInteractionMessageList)).isError()) {
            throw new RuntimeException();
        }
    }

    @RabbitListener(queuesToDeclare = @Queue(name = "video.favorite.queue"), containerFactory = "batchContainerFactory")
    public void handleVideoFavoriteMessage(List<VideoInteractionMessage> videoInteractionMessageList) {
        // 发起请求
        if (interactionPrivateClient.favoriteBatch(createInteractionBatchRequest(videoInteractionMessageList)).isError()) {
            throw new RuntimeException();
        }
    }
}
