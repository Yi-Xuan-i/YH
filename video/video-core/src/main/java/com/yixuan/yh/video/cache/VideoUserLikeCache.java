package com.yixuan.yh.video.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserCollectionsItem;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class VideoUserLikeCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VideoUserLikeMapper videoUserLikeMapper;
    @Autowired
    private RedisScript<Long> interactionScript;
    @Autowired
    private RedissonClient redissonClient;

    public boolean tryLike(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;

        Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "1", "600");
        if (result != null && result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
            RLock rLock = redissonClient.getLock(RedisConstant.VIDEO_USER_LIKE_LOCK_PREFIX + userId + ":" + videoId);
            rLock.lock();
            try {
                result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "1", "600");
                if (result != null && !result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                    return !result.equals(RedisConstant.InteractionLua.ERROR.getValue());
                }
                result = videoUserLikeMapper.isLike(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));

                return result > 0;
            } finally {
                rLock.unlock();
            }
        } else return result != null && !result.equals(RedisConstant.InteractionLua.ERROR.getValue());
    }

    public boolean tryUnlike(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;

        Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "0", "600");
        if (result != null && result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
            RLock rLock = redissonClient.getLock(RedisConstant.VIDEO_USER_LIKE_LOCK_PREFIX + userId + ":" + videoId);
            rLock.lock();
            try {
                result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "0", "600");
                if (result != null && !result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                    return !result.equals(RedisConstant.InteractionLua.ERROR.getValue());
                }
                result = videoUserLikeMapper.isLike(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));

                return result > 0;
            } finally {
                rLock.unlock();
            }
        } else return result != null && !result.equals(RedisConstant.InteractionLua.ERROR.getValue());
    }

}
