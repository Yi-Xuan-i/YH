package com.yixuan.yh.chat.websocket.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.response.ChatMessageMediaResponse;
import com.yixuan.yh.chat.websocket._enum.ChatReceiveMessageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatReceiveMessage {
    private final ChatReceiveMessageType type = ChatReceiveMessageType.NORMAL;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conversationId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;
    private String content;
    private ChatMessage.MessageType messageType;
    private ChatMessageMediaResponse media;
    private LocalDateTime sentTime;
}
