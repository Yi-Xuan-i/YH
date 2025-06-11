package com.yixuan.yh.live.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("live")
@Data
public class Live {
    Long roomId;
    Long anchorId;
    String clientId;
}
