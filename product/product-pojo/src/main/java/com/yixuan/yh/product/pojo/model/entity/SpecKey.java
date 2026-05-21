package com.yixuan.yh.product.pojo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("spec_key")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecKey {
    @TableId(type = IdType.ASSIGN_ID)
    private Long keyId;
    private String keyName;
}
