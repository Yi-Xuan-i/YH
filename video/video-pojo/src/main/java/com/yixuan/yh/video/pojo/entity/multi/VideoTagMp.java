package com.yixuan.yh.video.pojo.entity.multi;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("video_tag_mp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoTagMp {
    Long videoId;
    Long tagId;
}
