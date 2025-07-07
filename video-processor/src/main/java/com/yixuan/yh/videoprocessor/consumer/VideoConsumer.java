package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.feign.VideoPrivateClient;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import com.yixuan.yh.videoprocessor.mq.VideoLikeMessage;
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
public class VideoConsumer {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private VideoPrivateClient videoPrivateClient;

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
        if (result.getData() == null) {

        }

    }

    @RabbitListener(queuesToDeclare = @Queue(name = "video.like.queue"), containerFactory = "batchContainerFactory")
    public void handleVideoLikeMessage(List<VideoLikeMessage> videoLikeMessageList) {
        // 计算视频需要增加或减少的点赞数（并转换点赞记录对象）
        List<VideoLikeBatchRequest.LikeRecord> likeRecordList = new ArrayList<>(videoLikeMessageList.size());
        Map<Long, Long> videoLikeNumberMap = new HashMap<>();
        for (VideoLikeMessage videoLikeMessage : videoLikeMessageList) {
            if (!videoLikeNumberMap.containsKey(videoLikeMessage.getVideoId())) {
                videoLikeNumberMap.put(videoLikeMessage.getVideoId(), 0L);
            }
            videoLikeNumberMap.replace(videoLikeMessage.getVideoId(), videoLikeNumberMap.get(videoLikeMessage.getVideoId()) + videoLikeMessage.getStatus().getValue());

            VideoLikeBatchRequest.LikeRecord likeRecord = new VideoLikeBatchRequest.LikeRecord();
            likeRecord.setUserId(videoLikeMessage.getUserId());
            likeRecord.setVideoId(videoLikeMessage.getVideoId());
            likeRecord.setStatus(VideoLikeBatchRequest.LikeRecord.Status.valueOf(videoLikeMessage.getStatus().name()));

            likeRecordList.add(likeRecord);
        }
        // 构建点赞数对象
        List<VideoLikeBatchRequest.LikeIncr> likeIncrList = new ArrayList<>(videoLikeMessageList.size());
        for (Map.Entry<Long, Long> entry : videoLikeNumberMap.entrySet()) {
            VideoLikeBatchRequest.LikeIncr likeIncr = new VideoLikeBatchRequest.LikeIncr();
            likeIncr.setVideoId(entry.getKey());
            likeIncr.setIncrNumber(entry.getValue());

            likeIncrList.add(likeIncr);
        }
        // 构建完整对象
        VideoLikeBatchRequest videoLikeBatchRequest = new VideoLikeBatchRequest();
        videoLikeBatchRequest.setLikeRecordList(likeRecordList);
        videoLikeBatchRequest.setLikeIncrList(likeIncrList);
        // 发起请求
        if (videoPrivateClient.likeBatch(videoLikeBatchRequest).isError()) {
            throw new RuntimeException();
        }
    }
}
