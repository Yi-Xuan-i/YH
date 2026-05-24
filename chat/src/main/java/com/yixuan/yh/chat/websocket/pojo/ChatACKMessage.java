package com.yixuan.yh.chat.websocket.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yixuan.yh.chat.websocket._enum.ChatReceiveMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatACKMessage {
    private final ChatReceiveMessageType type = ChatReceiveMessageType.ACK;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conversationId;
    private String clientMsgId;
}
