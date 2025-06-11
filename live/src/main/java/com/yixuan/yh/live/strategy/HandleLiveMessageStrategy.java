package com.yixuan.yh.live.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.apache.coyote.BadRequestException;

import java.util.Map;

public interface HandleLiveMessageStrategy {
    LiveMessage handle(Map<String, Object> attributes, Long roomId, LiveMessage liveMessage) throws JsonProcessingException, BadRequestException;
}
