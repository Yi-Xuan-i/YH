package com.yixuan.yh.product.pojo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@TableName("product_sku")
@Data
@Accessors(chain = true)
public class ProductSku {
    @TableId(type = IdType.ASSIGN_ID)
    private Long skuId;
    private Long productId;
    private BigDecimal price;
    private Integer stock;
}