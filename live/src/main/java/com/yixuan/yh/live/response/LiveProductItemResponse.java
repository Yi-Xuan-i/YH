package com.yixuan.yh.live.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveProductItemResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;
    private String title;
    private String imageUrl;
    private Integer salesVolume;
}
