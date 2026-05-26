package com.yixuan.yh.chat.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ChatMediaPresignResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mediaId;
    private Integer mediaType;
    private String objectKey;
    private String uploadUrl;
    private String accessUrl;
    private String coverObjectKey;
    private String coverUploadUrl;
    private String coverAccessUrl;
}
