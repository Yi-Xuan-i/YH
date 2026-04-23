package com.yixuan.yh.videoprocessor.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.video.feign.InteractionPrivateClient;
import com.yixuan.yh.video.feign.VideoTagPrivateClient;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.pojo.mq.VideoCommentMessage;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.videoprocessor.constant.RabbitMQConstant;
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
}
