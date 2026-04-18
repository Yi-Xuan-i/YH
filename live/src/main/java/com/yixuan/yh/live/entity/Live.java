package com.yixuan.yh.live.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@TableName("live")
@Data
public class Live {
    @TableId(type = IdType.ASSIGN_ID)
    private Long roomId;
    private Long anchorId;
    private String clientId;
    private String title;
    private String coverUrl;
}
