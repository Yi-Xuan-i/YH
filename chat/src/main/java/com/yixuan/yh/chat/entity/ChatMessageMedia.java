package com.yixuan.yh.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("chat_message_media")
@Data
public class ChatMessageMedia {

    /**
     * 媒体id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属消息id
     */
    private Long messageId;

    /**
     * 媒体类型
     */
    private Integer mediaType;

    /**
     * 视频url
     */
    private String url;

    /**
     * 视频封面url
     */
    private String coverUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}