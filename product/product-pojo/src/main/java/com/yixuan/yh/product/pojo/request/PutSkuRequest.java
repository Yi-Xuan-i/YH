package com.yixuan.yh.product.pojo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PutSkuRequest {

    List<PutSku> changeSkuList;

    @Data
    public static class PutSku {
        Long skuId;
        BigDecimal price;
        Integer stock;
    }
}
