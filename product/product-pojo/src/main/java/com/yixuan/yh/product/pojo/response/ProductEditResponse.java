package com.yixuan.yh.product.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yixuan.yh.product.pojo.model.entity.ProductCarousel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductEditResponse {
    private String title;
    private String coverUrl;
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long defaultSkuId;
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
        @JsonSerialize(using = ToStringSerializer.class)
        private Long skuId;
        private BigDecimal price;
        private Integer stock;
        private List<Carousel> carouselList;
        private List<SpecPair> specs;

        // 规格键值对
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SpecPair {
            private String key;
            private String value;
            @JsonSerialize(using = ToStringSerializer.class)
            private Long keyId;
            @JsonSerialize(using = ToStringSerializer.class)
            private Long valueId;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Carousel {
            @JsonSerialize(using = ToStringSerializer.class)
            private Long id;
            private String url;
        }
    }
}
