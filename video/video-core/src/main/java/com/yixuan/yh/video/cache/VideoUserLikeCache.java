package com.yixuan.yh.video.cache;

import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
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

    public boolean tryLike(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;

        while (true) {
            Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "1");
            assert result != null;
            if (result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                result = videoUserLikeMapper.isLike(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));
            } else if (result.equals(RedisConstant.InteractionLua.ERROR.getValue())) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean tryUnlike(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;

        while (true) {
            Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "0");
            assert result != null;
            if (result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                result = videoUserLikeMapper.isLike(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));
            } else if (result.equals(RedisConstant.InteractionLua.ERROR.getValue())) {
                return false;
            } else {
                return true;
            }
        }
    }

}
