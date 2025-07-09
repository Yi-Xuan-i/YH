package com.yixuan.yh.video.mq;

import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoInteractionMessage {
    Long userId;
    Long videoId;
    InteractionStatus status;
}
