package com.yixuan.yh.video.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息离线发送表（本地消息表）
 */
@Data
@TableName("message_outbox")
public class MessageOutbox {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务 ID
     */
    private Long businessId;

    /**
     * 交换机名称
     */
    private String exchangeName;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 消息体内容
     */
    private String messageBody;

    /**
     * 状态：例如 0-待发送，1-发送成功，2-发送失败
     */
    private OutboxStatus status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public enum OutboxStatus {
        PENDING(0, "待发送"),
        SUCCESS(1, "发送成功"),
        FAILED(2, "发送失败");

        @EnumValue
        private final int code;
        private final String desc;
    }
}