package com.yixuan.yh.chat.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageMediaResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Integer mediaType;
    private String url;
    private String coverUrl;
}
