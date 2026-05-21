package com.yixuan.yh.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("user_follow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollow {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long followerId;
    private Long followeeId;
    private LocalDateTime createdTime;
}
