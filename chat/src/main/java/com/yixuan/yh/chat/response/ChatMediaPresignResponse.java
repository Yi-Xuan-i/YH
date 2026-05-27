package com.yixuan.yh.chat.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ChatMediaPresignResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mediaId;
    private String uploadUrl;
    private String coverUploadUrl;
}
