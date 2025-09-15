package com.yixuan.yh.video.consumer;

import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.videoprocessor.mq.VideoCommentIncrMessage;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoCommentConsumer {

    private final VideoMapper videoMapper;

    public VideoCommentConsumer(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = "video.comment.direct"),
                    value = @Queue(name = "video.comment.incr.queue"),
                    key = "video.comment.incr.queue"
            )
    )
    public void handleVideoIncrCommentMessage(List<VideoCommentIncrMessage> videoCommentIncrMessages) {
        videoMapper.updateCommentBatch(videoCommentIncrMessages);
    }

}
