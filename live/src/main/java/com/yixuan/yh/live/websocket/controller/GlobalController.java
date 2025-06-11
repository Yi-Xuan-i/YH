package com.yixuan.yh.live.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.live.strategy.HandleLiveMessageStrategy;
import com.yixuan.yh.live.websocket.pojo.ChatSendMessage;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class GlobalController {

    @Autowired
    private Map<String, HandleLiveMessageStrategy> handleLiveMessageStrategyMap;

    @MessageMapping("/global/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public LiveMessage handleMessage(
            @DestinationVariable Long roomId,
            LiveMessage liveMessage,  @Header("simpSessionAttributes") Map<String, Object> attributes) throws JsonProcessingException, BadRequestException {

        return handleLiveMessageStrategyMap.get(liveMessage.getType().name()).handle(attributes, roomId, liveMessage);
    }
}