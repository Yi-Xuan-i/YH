package com.yixuan.yh.product.pojo.model.multi;

import lombok.Data;

@Data
public class SkuSpecInfo {
    private Long skuId;
    private String specKey;
    private String specValue;
    private Long specKeyId;
    private Long specValueId;
}
