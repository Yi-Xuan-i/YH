package com.yixuan.yh.video.consumer;

import com.yixuan.yh.video.mapper.VideoMapper;
import com.yixuan.yh.video.mapper.VideoUserCommentMapper;
import com.yixuan.yh.videoprocessor.mq.VideoCommentIncrMessage;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VideoCommentConsumer {

    private final VideoMapper videoMapper;
    private final VideoUserCommentMapper videoUserCommentMapper;

    public VideoCommentConsumer(VideoMapper videoMapper, VideoUserCommentMapper videoUserCommentMapper) {
        this.videoMapper = videoMapper;
        this.videoUserCommentMapper = videoUserCommentMapper;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = "video.comment.direct"),
                    value = @Queue(name = "video.comment.incr.queue"),
                    key = "video.comment.incr.queue"
            )
    )
    @Transactional
    public void handleVideoIncrCommentMessage(VideoCommentIncrMessage videoCommentIncrMessage) {
        if (!videoCommentIncrMessage.getCommentIncrList().isEmpty()) { // 常理来说这里不会为null
            videoMapper.updateCommentBatch(videoCommentIncrMessage.getCommentIncrList());
        }
        if (!videoCommentIncrMessage.getReplyIncrList().isEmpty()) {
            videoUserCommentMapper.updateReplyBatch(videoCommentIncrMessage.getReplyIncrList());
        }
    }

}
