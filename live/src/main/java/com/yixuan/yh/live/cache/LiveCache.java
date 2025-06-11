package com.yixuan.yh.live.cache;

import com.yixuan.yh.live.mapper.LiveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class LiveCache {

    @Autowired
    private LiveMapper liveMapper;

    /**
     * AnchorId 不会改变，缓存无需考虑更新。
     */
    @Cacheable(value = "roomAnchorId", key = "#roomId")
    public Long getAnchorId(Long roomId) {
        return liveMapper.selectAnchorIdById(roomId);
    }

}
