package com.yixuan.yh.order.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostOrderResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    Long orderId;
    String payUrl;
}
