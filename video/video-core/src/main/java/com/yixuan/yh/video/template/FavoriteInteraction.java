package com.yixuan.yh.video.template;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mapper.VideoUserFavoriteMapper;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.cache.VideoUserFavoriteCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class FavoriteInteraction extends InteractionTemplate {
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final VideoUserFavoriteCache videoUserFavoriteCache;
    private final SnowflakeUtils snowflakeUtils;
    private final RabbitTemplate rabbitTemplate;
    private final VideoUserFavoriteMapper videoUserFavoriteMapper;

    public FavoriteInteraction(RedissonClient redissonClient, StringRedisTemplate stringRedisTemplate, VideoUserFavoriteCache videoUserFavoriteCache, SnowflakeUtils snowflakeUtils, RabbitTemplate rabbitTemplate, VideoUserFavoriteMapper videoUserFavoriteMapper) {
        super();
        this.redissonClient = redissonClient;
        this.stringRedisTemplate = stringRedisTemplate;
        this.videoUserFavoriteCache = videoUserFavoriteCache;
        this.snowflakeUtils = snowflakeUtils;
        this.rabbitTemplate = rabbitTemplate;
        this.videoUserFavoriteMapper = videoUserFavoriteMapper;
    }

    @Override
    protected void tryInteract(Long userId, Long videoId, InteractionStatus status) throws Exception {
        // 判断当前操作是否合法
        if (!((status.equals(InteractionStatus.FRONT) && videoUserFavoriteCache.tryFavorite(userId, videoId)) ||
                (status.equals(InteractionStatus.BACK) && videoUserFavoriteCache.tryUnFavorite(userId, videoId)))) {
            throw new Exception("异常操作！");
        }
    }

    @Override
    protected void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage) {
        // 信息发送到消息队列异步处理
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_FAVORITE_QUEUE, videoInteractionMessage);
    }
}
