package com.yixuan.yh.video.template;

import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.cache.VideoUserLikeCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeInteraction extends InteractionTemplate {
    private final VideoUserLikeCache videoUserLikeCache;
    private final RabbitTemplate rabbitTemplate;

    @Override
    protected void tryInteract(Long userId, Long videoId, InteractionStatus status) {
        // 判断当前操作是否合法
        if (!((status.equals(InteractionStatus.FRONT) && videoUserLikeCache.tryLike(userId, videoId))||
                (status.equals(InteractionStatus.BACK) && videoUserLikeCache.tryUnlike(userId, videoId)))) {
            throw new YHClientException("异常操作！");
        }

    }

    @Override
    protected void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage) {
        // 信息发送到消息队列异步处理
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoInteractionMessage);
    }
}
