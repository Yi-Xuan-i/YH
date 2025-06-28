package com.yixuan.yh.product.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductDetailResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private final Long productId;
    private final Long merchantId;
    private final String title;
    private final String description;
    private final List<Sku> skus;
    private final List<String> carousels;

    // 私有构造方法，只能通过建造者创建
    private ProductDetailResponse(Builder builder) {
        this.productId = builder.productId;
        this.merchantId = builder.merchantId;
        this.title = builder.title;
        this.description = builder.description;
        this.skus = builder.skus;
        this.carousels = builder.carousels;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long productId;
        private Long merchantId;
        private String title;
        private String description;
        private List<Sku> skus;
        private List<String> carousels;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder merchantId(Long merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder skus(List<Sku> skus) {
            this.skus = skus;
            return this;
        }

        public Builder carousels(List<String> carousels) {
            this.carousels = carousels;
            return this;
        }

        public ProductDetailResponse build() {
            return new ProductDetailResponse(this);
        }
    }

    @Getter
    public static class Sku {
        private final Long skuId;
        private final BigDecimal price;
        private final Integer stock;
        private final List<Spec> specs;

        private Sku(SkuBuilder builder) {
            this.skuId = builder.skuId;
            this.price = builder.price;
            this.stock = builder.stock;
            this.specs = builder.specs;
        }

        public static SkuBuilder builder() {
            return new SkuBuilder();
        }

        public static class SkuBuilder {
            private Long skuId;
            private BigDecimal price;
            private Integer stock;
            private List<Spec> specs;

            public SkuBuilder skuId(Long skuId) {
                this.skuId = skuId;
                return this;
            }

            public SkuBuilder price(BigDecimal price) {
                this.price = price;
                return this;
            }

            public SkuBuilder stock(Integer stock) {
                this.stock = stock;
                return this;
            }

            public SkuBuilder specs(List<Spec> specs) {
                this.specs = specs;
                return this;
            }

            public Sku build() {
                return new Sku(this);
            }
        }
    }

    @Getter
    public static class Spec {
        private final String key;
        private final String value;

        public Spec(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
