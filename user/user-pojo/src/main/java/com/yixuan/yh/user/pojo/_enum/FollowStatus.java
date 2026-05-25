package com.yixuan.yh.user.pojo._enum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowStatus {
    NOT_FOLLOWED(0),
    FOLLOWED(1),
    FOLLOWED_BY(2),
    MUTUAL_FOLLOWED(3);

    private final int code;

    @JsonValue
    public int getCode() {
        return code;
    }
}
