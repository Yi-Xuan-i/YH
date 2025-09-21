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
        /* 统计视频增加的评论数 */
        Map<Long, Integer> commentIncrMap = new HashMap<>(videoCommentMessageList.size());

        videoCommentMessageList.forEach(videoCommentMessage -> {
            Long videoId = videoCommentMessage.getVideoId();
            if (!commentIncrMap.containsKey(videoId)) {
                commentIncrMap.put(videoId, 0);
            }
            commentIncrMap.replace(videoId, commentIncrMap.get(videoId) + 1);
        });

        List<VideoCommentIncrMessage.CommentIncr> commentIncrList = new ArrayList<>(videoCommentMessageList.size());
        for (Map.Entry<Long, Integer> entry : commentIncrMap.entrySet()) {
            VideoCommentIncrMessage.CommentIncr commentIncr = new VideoCommentIncrMessage.CommentIncr();
            commentIncr.setVideoId(entry.getKey());
            commentIncr.setIncrNumber(entry.getValue());

            commentIncrList.add(commentIncr);
        }

        /* 统计评论增加的回复数 */
        Map<Long, Integer> replyIncrMap = new HashMap<>(videoCommentMessageList.size());

        videoCommentMessageList.forEach(videoCommentMessage -> {
            Long parentId = videoCommentMessage.getParentId();
            if (parentId != null) {
                if (!replyIncrMap.containsKey(parentId)) {
                    replyIncrMap.put(parentId, 0);
                }
                replyIncrMap.replace(parentId, replyIncrMap.get(parentId) + 1);
            }
        });

        List<VideoCommentIncrMessage.ReplyIncr> replyIncrList = new ArrayList<>(videoCommentMessageList.size());
        for (Map.Entry<Long, Integer> entry : replyIncrMap.entrySet()) {
            VideoCommentIncrMessage.ReplyIncr replyIncr = new VideoCommentIncrMessage.ReplyIncr();
            replyIncr.setParentId(entry.getKey());
            replyIncr.setIncrNumber(entry.getValue());

            replyIncrList.add(replyIncr);
        }

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_COMMENT_DIRECT_EXCHANGE, RabbitMQConstant.VIDEO_COMMENT_INCR_QUEUE, new VideoCommentIncrMessage(commentIncrList, replyIncrList));
    }

}
