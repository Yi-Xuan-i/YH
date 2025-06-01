package com.yixuan.yh.product.request;

import lombok.Data;

import java.util.List;

@Data
public class PostSkuSpecRequest {
    Long productId;
    String type; // old、new
    Spec spec;
    List<List<SpecId>> skus;

    @Data
    public static class Spec {
        private String key;
        private String value;
        private Long keyId; // 用于非新增规格时（已有规格插入新值）
    }

    @Data
    public static class SpecId {
        private Long keyId;
        private Long valueId;
    }
}
