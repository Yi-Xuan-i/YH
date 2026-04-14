package com.yixuan.yh.videoprocessor.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.video.feign.InteractionPrivateClient;
import com.yixuan.yh.video.feign.VideoTagPrivateClient;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.videoprocessor.constant.RabbitMQConstant;
import com.yixuan.yh.video.pojo.mq.VideoCommentIncrMessage;
import com.yixuan.yh.videoprocessor.entity.UserVideoInteraction;
import com.yixuan.yh.videoprocessor.serivce.UserVideoInteractionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VideoInteractionConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final InteractionPrivateClient interactionPrivateClient;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final UserVideoInteractionService userVideoInteractionService;
    private final ResourceLoader resourceLoader;
    private final VideoTagPrivateClient videoTagPrivateClient;
    private String updateTagsLua;

    @PostConstruct
    public void init() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:lua/update_tags.lua");
        this.updateTagsLua = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = RabbitMQConstant.VIDEO_INTERACTION_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = RabbitMQConstant.VIDEO_INTERACTION_QUEUE_KEY,
                    value = @Queue(name = RabbitMQConstant.VIDEO_INTERACTION_QUEUE)
            ),
            containerFactory = "batchContainerFactory"
    )
    public void handleVideoInteractionMessage(List<Message> messageList) {
        RBatch batch = redissonClient.createBatch();
        List<UserVideoInteraction> userVideoInteractionList = new ArrayList<>(messageList.size());
        for (Message message : messageList) {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();

            if (routingKey.equals(RabbitMQConstant.VIDEO_COMMENT_QUEUE_KEY)) {
                try {
                    VideoCommentMessage videoCommentMessage = objectMapper.readValue(new String(message.getBody(), StandardCharsets.UTF_8), VideoCommentMessage.class);
                    updateTags(batch, userVideoInteractionList, videoCommentMessage.getUserId(), videoCommentMessage.getVideoId(),
                            UserVideoInteraction.InteractionType.COMMENT);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            } else if (routingKey.equals(RabbitMQConstant.VIDEO_LIKE_QUEUE_KEY) || routingKey.equals(RabbitMQConstant.VIDEO_FAVORITE_QUEUE_KEY)) {
                try {
                    VideoInteractionMessage videoInteractionMessage = objectMapper.readValue(new String(message.getBody(), StandardCharsets.UTF_8), VideoInteractionMessage.class);
                    if (videoInteractionMessage.getStatus() == InteractionStatus.BACK) {
                        return;
                    }
                    updateTags(batch, userVideoInteractionList, videoInteractionMessage.getUserId(), videoInteractionMessage.getVideoId(),
                            routingKey.equals(RabbitMQConstant.VIDEO_LIKE_QUEUE_KEY) ? UserVideoInteraction.InteractionType.LIKE : UserVideoInteraction.InteractionType.COLLECT);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 批量执行lua脚本
        batch.execute();
        // 批量插入长期记录
        userVideoInteractionService.saveBatch(userVideoInteractionList);
    }

    private void updateTags(RBatch batch, List<UserVideoInteraction> userVideoInteractionList, Long userId, Long videoId, UserVideoInteraction.InteractionType interactionType) {
        // 获取视频对应标签
        List<Long> tagList = videoTagPrivateClient.getVideoTags(videoId).getData();

        // 执行脚本
        String key = "user:video:tags:" + userId;

        // 准备参数
        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis() / 1000);    // ARGV[1]: now
        args.add(Math.log(2) / (60 * 15));              // ARGV[2]: alpha
        args.add(50);                                   // ARGV[3]: keep
        args.add(100);                                  // ARGV[4]: threshold
        args.add(24 * 3600);                            // ARGV[5]: expire
        args.add(interactionType.getCode());            // ARGV[6]: weight
        args.addAll(tagList);

        // 使用 eval 方法
        RScript script = batch.getScript(StringCodec.INSTANCE);
        script.evalAsync(
                RScript.Mode.READ_WRITE,
                updateTagsLua,
                RScript.ReturnType.VALUE,
                Collections.singletonList(key),
                args.toArray(new Object[0])
        );


        // 长期记录
        UserVideoInteraction userVideoInteraction = new UserVideoInteraction();
        userVideoInteraction.setUserId(userId);
        userVideoInteraction.setVideoId(videoId);
        userVideoInteraction.setType(interactionType);
        userVideoInteractionList.add(userVideoInteraction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = RabbitMQConstant.VIDEO_INTERACTION_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = RabbitMQConstant.VIDEO_COMMENT_QUEUE_KEY,
                    value = @Queue(name = RabbitMQConstant.VIDEO_COMMENT_QUEUE)
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

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = RabbitMQConstant.VIDEO_INTERACTION_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = RabbitMQConstant.VIDEO_LIKE_QUEUE_KEY,
                    value = @Queue(name = RabbitMQConstant.VIDEO_LIKE_QUEUE)
            ),
            containerFactory = "batchContainerFactory"
    )
    public void handleVideoLikeMessage(List<VideoInteractionMessage> videoInteractionMessageList) {
        // 发起请求
        if (interactionPrivateClient.likeBatch(createInteractionBatchRequest(videoInteractionMessageList)).isError()) {
            throw new RuntimeException();
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(name = RabbitMQConstant.VIDEO_INTERACTION_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
                    key = RabbitMQConstant.VIDEO_FAVORITE_QUEUE_KEY,
                    value = @Queue(name = RabbitMQConstant.VIDEO_FAVORITE_QUEUE)
            ),
            containerFactory = "batchContainerFactory"
    )
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
