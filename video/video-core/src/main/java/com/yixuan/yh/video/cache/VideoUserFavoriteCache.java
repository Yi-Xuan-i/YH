package com.yixuan.yh.video.cache;

import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserFavoriteMapper;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class VideoUserFavoriteCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VideoUserFavoriteMapper videoUserFavoriteMapper;
    @Autowired
    private RedisScript<Long> interactionScript;

    public boolean tryFavorite(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_FAVORITE_KEY_PREFIX + userId;

        while (true) {
            Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "1");
            assert result != null;
            if (result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                result = videoUserFavoriteMapper.isFavorite(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));
            } else if (result.equals(RedisConstant.InteractionLua.ERROR.getValue())) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean tryUnFavorite(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_FAVORITE_KEY_PREFIX + userId;

        while (true) {
            Long result = stringRedisTemplate.execute(interactionScript, Collections.singletonList(key), videoId.toString(), "0");
            assert result != null;
            if (result.equals(RedisConstant.InteractionLua.NOT_EXIST.getValue())) {
                result = videoUserFavoriteMapper.isFavorite(userId, videoId) ? 1L : 0L;
                stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));
            } else if (result.equals(RedisConstant.InteractionLua.ERROR.getValue())) {
                return false;
            } else {
                return true;
            }
        }
    }



}
