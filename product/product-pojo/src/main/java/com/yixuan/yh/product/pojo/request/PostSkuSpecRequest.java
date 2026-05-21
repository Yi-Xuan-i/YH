package com.yixuan.yh.product.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSkuSpecRequest {
    private Long productId;
    private String type; // old、new
    private Spec spec;
    private List<List<SpecId>> skus;

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