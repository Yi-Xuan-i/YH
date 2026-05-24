package com.yixuan.yh.user.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ProfileBasicResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    Long id;
    String avatarUrl;
    String name;
}
