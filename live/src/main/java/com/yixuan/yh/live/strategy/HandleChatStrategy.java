package com.yixuan.yh.live.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.live.websocket.pojo.ChatSendMessage;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CHAT")
public class HandleChatStrategy implements HandleLiveMessageStrategy {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public LiveMessage handle(Map<String, Object> attributes, Long roomId, LiveMessage liveMessage) throws JsonProcessingException {
        ChatSendMessage chatSendMessage = new ChatSendMessage();
        chatSendMessage.setId((Long) attributes.get("id"));
        chatSendMessage.setName(attributes.get("id").toString());
        chatSendMessage.setContent((String) liveMessage.getContent());

        liveMessage.setContent(objectMapper.writeValueAsString(chatSendMessage));
        return liveMessage;
    }
}
