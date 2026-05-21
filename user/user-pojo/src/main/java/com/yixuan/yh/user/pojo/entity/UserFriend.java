package com.yixuan.yh.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("user_friend")
@Data
public class UserFriend {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long friendId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}