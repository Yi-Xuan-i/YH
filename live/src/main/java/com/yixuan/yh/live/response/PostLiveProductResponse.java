package com.yixuan.yh.live.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLiveProductResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String imageUrl;
}
