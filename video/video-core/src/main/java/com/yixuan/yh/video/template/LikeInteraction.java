package com.yixuan.yh.video.template;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.cache.VideoUserLikeCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.locks.Lock;

@Component
public class LikeInteraction extends InteractionTemplate {
    private final RedissonClient redissonClient;
    private final VideoUserLikeCache videoUserLikeCache;
    private final SnowflakeUtils snowflakeUtils;
    private final VideoUserLikeMapper videoUserLikeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisScript<Integer> interactionScript;

    public LikeInteraction(RedissonClient redissonClient, VideoUserLikeCache videoUserLikeCache, SnowflakeUtils snowflakeUtils, VideoUserLikeMapper videoUserLikeMapper, StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate, RedisScript interactionScript) {
        super();
        this.redissonClient = redissonClient;
        this.videoUserLikeCache = videoUserLikeCache;
        this.snowflakeUtils = snowflakeUtils;
        this.videoUserLikeMapper = videoUserLikeMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.interactionScript = interactionScript;
    }

    @Override
    protected void tryInteract(Long userId, Long videoId, InteractionStatus status) throws BadRequestException {
        // 判断当前操作是否合法
        if (!((status.equals(InteractionStatus.FRONT) && videoUserLikeCache.tryLike(userId, videoId))||
                (status.equals(InteractionStatus.BACK) && videoUserLikeCache.tryUnlike(userId, videoId)))) {
            throw new BadRequestException("异常操作！");
        }

    }

    @Override
    protected void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage) {
        // 信息发送到消息队列异步处理
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoInteractionMessage);
    }
}
