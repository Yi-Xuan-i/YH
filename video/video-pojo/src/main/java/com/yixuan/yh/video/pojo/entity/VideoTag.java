package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("video_tag")
@Data
public class VideoTag {
    @TableId(type = IdType.ASSIGN_ID)
    Long id;
    String name;
    LocalDateTime createdTime;
}
