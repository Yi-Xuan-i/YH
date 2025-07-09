package com.yixuan.yh.video.pojo.entity;

import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUserFavorite {
    Long id;
    Long userId;
    Long videoId;
    InteractionStatus status;
}