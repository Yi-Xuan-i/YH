package com.yixuan.yh.video.cache;

import com.yixuan.yh.video.constant.RedisConstant;
import com.yixuan.yh.video.mapper.VideoUserLikeMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class VideoUserLikeCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VideoUserLikeMapper videoUserLikeMapper;

    public boolean isLike(Long userId, Long videoId, VideoUserLike.Status targetStatus) {
        String key = RedisConstant.VIDEO_USER_LIKE_KEY_PREFIX + userId;
        String strResult = (String) stringRedisTemplate.opsForHash().get(key, videoId.toString());
        int result;
        if (strResult == null) {
            result = videoUserLikeMapper.isLike(userId, videoId) ? 1 : 0;
            // 如果当前操作不合法才进行缓存，因为如果合法后续修改点赞状态会进行缓存
            if (!((result == 1 && targetStatus.equals(VideoUserLike.Status.LIKE)) || (result == 0 && targetStatus.equals(VideoUserLike.Status.UNLIKE)))) {
                stringRedisTemplate.opsForHash().put(key, videoId, String.valueOf(result));
            }
        } else {
            result = Integer.parseInt(strResult);
        }
        return result == 1;
    }

}
