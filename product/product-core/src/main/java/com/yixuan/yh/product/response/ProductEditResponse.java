package com.yixuan.yh.product.response;

import com.yixuan.yh.product.model.entity.ProductCarousel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductEditResponse {
    private String title;
    private String coverUrl;
    private String description;
    private BigDecimal price;
    private List<ProductCarousel> carouselFileList;
    private List<SkuDetailDTO> productSkuList;

//    @Data
//    @Accessors(chain = true)
//    public static class ProductSku {
//        private String spec;
//        private BigDecimal price;
//        private Integer stock;
//    }

    @Data
    @AllArgsConstructor
    public static class SkuDetailDTO {
        private Long skuId;
        private BigDecimal price;
        private Integer stock;
        private List<SpecPair> specs;

        // 规格键值对
        @Data
        @AllArgsConstructor
        public static class SpecPair {
            private String key;
            private String value;
            private Long keyId;
            private Long valueId;
        }
    }
}
