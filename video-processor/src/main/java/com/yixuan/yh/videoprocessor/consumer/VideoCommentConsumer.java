package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.videoprocessor.constant.RabbitMQConstant;
import com.yixuan.yh.videoprocessor.mq.VideoCommentIncrMessage;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VideoCommentConsumer {

    private final RabbitTemplate rabbitTemplate;

    public VideoCommentConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = "video.comment.fanout", type = ExchangeTypes.FANOUT),
                    value = @Queue(name = "video.comment.count.queue")
            ),
            containerFactory = "batchContainerFactory"
    )
    public void handleVideoCommentMessage(List<VideoCommentMessage> videoCommentMessageList) {
        Map<Long, Long> commentIncrMap = new HashMap<>(videoCommentMessageList.size());

        videoCommentMessageList.forEach((videoCommentMessage -> {
            Long videoId = videoCommentMessage.getVideoId();
            if (!commentIncrMap.containsKey(videoId)) {
                commentIncrMap.put(videoId, 0L);
            }
            commentIncrMap.replace(videoId, commentIncrMap.get(videoId) + 1);
        }));

        List<VideoCommentIncrMessage> videoCommentIncrMessageList = new ArrayList<>(videoCommentMessageList.size());
        for (Map.Entry<Long, Long> entry : commentIncrMap.entrySet()) {
            VideoCommentIncrMessage videoCommentIncrMessage = new VideoCommentIncrMessage();
            videoCommentIncrMessage.setVideoId(entry.getKey());
            videoCommentIncrMessage.setIncrNumber(entry.getValue());

            videoCommentIncrMessageList.add(videoCommentIncrMessage);
        }

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_COMMENT_DIRECT_EXCHANGE, RabbitMQConstant.VIDEO_COMMENT_INCR_QUEUE, videoCommentIncrMessageList);
    }

}
