package com.yixuan.yh.order.pojo.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yixuan.yh.order.pojo.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingPaymentOrderResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private BigDecimal paymentAmount;
    private Order.OrderStatus orderStatus;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemResponse {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long orderItemId;
        @JsonSerialize(using = ToStringSerializer.class)
        private Long productId;
        @JsonSerialize(using = ToStringSerializer.class)
        private Long skuId;
        private String sku;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}
