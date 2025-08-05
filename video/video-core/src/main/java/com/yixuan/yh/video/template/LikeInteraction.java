package com.yixuan.yh.video.template;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.video.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.cache.VideoUserLikeCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class LikeInteraction extends InteractionTemplate {
    private final RedissonClient redissonClient;
    private final VideoUserLikeCache videoUserLikeCache;
    private final SnowflakeUtils snowflakeUtils;
    private final VideoUserLikeMapper videoUserLikeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public LikeInteraction(RedissonClient redissonClient, VideoUserLikeCache videoUserLikeCache, SnowflakeUtils snowflakeUtils, VideoUserLikeMapper videoUserLikeMapper, StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate) {
        super();
        this.redissonClient = redissonClient;
        this.videoUserLikeCache = videoUserLikeCache;
        this.snowflakeUtils = snowflakeUtils;
        this.videoUserLikeMapper = videoUserLikeMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected Lock getLock(Long userId) {
        return redissonClient.getLock(RedisConstant.VIDEO_LIKE_LOCK_KEY_PREFIX + userId);
    }

    @Override
    protected void tryInteract(Long userId, Long videoId, InteractionStatus status) throws Exception {
        InteractionStatus legalStatus = status.equals(InteractionStatus.FRONT) ? InteractionStatus.BACK : InteractionStatus.FRONT;

        // 判断当前操作是否合法
        if (!((status.equals(InteractionStatus.FRONT) && !videoUserLikeCache.isLike(userId, videoId)) ||
                (status.equals(InteractionStatus.BACK) && videoUserLikeCache.isLike(userId, videoId)))) {
            throw new Exception("异常操作！");
        }
        // 保存到数据库
        VideoUserLike videoUserLike = new VideoUserLike();
        videoUserLike.setId(snowflakeUtils.nextId());
        videoUserLike.setUserId(userId);
        videoUserLike.setVideoId(videoId);
        videoUserLike.setStatus(status);
        videoUserLikeMapper.insert(videoUserLike);
        // 删除缓存
        stringRedisTemplate.opsForHash().delete(RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId, videoId.toString());
    }

    @Override
    protected void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage) {
        // 信息发送到消息队列异步处理
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_LIKE_QUEUE, videoInteractionMessage);
    }
}
