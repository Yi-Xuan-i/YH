package com.yixuan.yh.product.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailResponse {
    private Long productId;
    private Long merchantId;
    private String title;
    private String description;
    private List<Sku> skus;
    private List<String> carousels;

    @Data
    @AllArgsConstructor
    public static class Sku {
        Long skuId;
        BigDecimal price;
        Integer stock;
        List<Spec> specs;
    }

    @Data
    @AllArgsConstructor
    public static class Spec {
        String key;
        String value;
    }
}
