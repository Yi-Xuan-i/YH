package com.yixuan.yh.video.pojo._enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InteractionStatus {
    FRONT(1),
    BACK(-1);

    private final int value;
}
