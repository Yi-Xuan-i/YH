package com.yixuan.yh.live.strategy;

import com.yixuan.yh.live.cache.LiveCache;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("EXPLAIN")
public class HandleExplainStrategy implements HandleLiveMessageStrategy {

    @Autowired
    private LiveCache liveCache;

    @Override
    public LiveMessage handle(Map<String, Object> attributes, Long roomId, LiveMessage liveMessage) throws BadRequestException {
        // 这里要鉴权只能主播发送
        if (!attributes.get("id").equals(liveCache.getAnchorId(roomId))) {
            throw new BadRequestException("你没有权限！");
        }

        return liveMessage;
    }
}