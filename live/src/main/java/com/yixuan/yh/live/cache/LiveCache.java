package com.yixuan.yh.live.cache;

import com.yixuan.yh.live.mapper.LiveMapper;
import com.yixuan.yh.user.feign.UserPrivateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class LiveCache {

    @Autowired
    private LiveMapper liveMapper;
    @Autowired
    private UserPrivateClient userPrivateClient;

    /**
     * AnchorId 不会改变，缓存无需考虑更新。
     */
    @Cacheable(value = "roomAnchorId", key = "#roomId")
    public Long getAnchorId(Long roomId) {
        return liveMapper.selectAnchorIdById(roomId);
    }

    /**
     * UserName 会改变，但是对于直播聊天影响不是很大。
     */
    @Cacheable(value = "roomUserName", key = "#userId")
    public String getUserName(Long userId) {
        return userPrivateClient.getName(userId).getData();
    }

}
