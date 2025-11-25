package com.yixuan.yh.video.pojo.mq;

import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class VideoInteractionMessage {
    Long userId;
    Long videoId;
    InteractionStatus status;
}
