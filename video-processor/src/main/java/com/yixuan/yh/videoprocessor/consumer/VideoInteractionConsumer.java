package com.yixuan.yh.videoprocessor.consumer;

import com.yixuan.yh.video.feign.InteractionPrivateClient;
import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.videoprocessor.constant.RabbitMQConstant;
import com.yixuan.yh.video.pojo.mq.VideoCommentIncrMessage;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VideoInteractionConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final InteractionPrivateClient interactionPrivateClient;

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

        /* 统计直接评论增加的回复数 */
        Map<Long, Integer> replyIncrMap = new HashMap<>(videoCommentMessageList.size());

        videoCommentMessageList.forEach(videoCommentMessage -> {
            Long rootId = videoCommentMessage.getRootId();
            if (rootId != null) {
                if (!replyIncrMap.containsKey(rootId)) {
                    replyIncrMap.put(rootId, 0);
                }
                replyIncrMap.replace(rootId, replyIncrMap.get(rootId) + 1);
            }
        });

        List<VideoCommentIncrMessage.ReplyIncr> replyIncrList = new ArrayList<>(videoCommentMessageList.size());
        for (Map.Entry<Long, Integer> entry : replyIncrMap.entrySet()) {
            VideoCommentIncrMessage.ReplyIncr replyIncr = new VideoCommentIncrMessage.ReplyIncr();
            replyIncr.setRootId(entry.getKey());
            replyIncr.setIncrNumber(entry.getValue());

            replyIncrList.add(replyIncr);
        }

        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_COMMENT_DIRECT_EXCHANGE, RabbitMQConstant.VIDEO_COMMENT_INCR_QUEUE, new VideoCommentIncrMessage(commentIncrList, replyIncrList));
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


}
