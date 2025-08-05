package com.yixuan.yh.video.cache;

import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class VideoUserLikeCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VideoUserLikeMapper videoUserLikeMapper;

    public boolean isLike(Long userId, Long videoId) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;
        String strResult = (String) stringRedisTemplate.opsForHash().get(key, videoId.toString());
        int result;
        if (strResult == null) {
            result = videoUserLikeMapper.isLike(userId, videoId) ? 1 : 0;
            stringRedisTemplate.opsForHash().put(key, videoId.toString(), String.valueOf(result));
        } else {
            result = Integer.parseInt(strResult);
        }
        return result == 1;
    }

}
