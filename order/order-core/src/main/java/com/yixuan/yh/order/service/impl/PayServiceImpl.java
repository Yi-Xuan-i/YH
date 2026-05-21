package com.yixuan.yh.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.order.mapper.OrderItemMapper;
import com.yixuan.yh.order.mapper.OrderMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import com.yixuan.yh.order.pojo.entity.OrderItem;
import com.yixuan.yh.order.properties.AliPayProperties;
import com.yixuan.yh.order.service.PayService;
import com.yixuan.yh.product.feign.ProductPrivateClient;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductPrivateClient productPrivateClient;
    private final AliPayProperties aliPayProperties;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public String payNotify(Map<String, String> params) throws AlipayApiException {
        boolean verifyResult = AlipaySignature.rsaCheckV1(
                params,
                aliPayProperties.getAlipayPublicKey(),
                "UTF-8",
                "RSA2"
        );

        if (!verifyResult) {
            return "fail";
        }

        Long orderId = Long.valueOf(params.get("out_trade_no"));
        String totalAmount = params.get("total_amount");
        String tradeStatus = params.get("trade_status");

        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            Order.OrderStatus orderStatus = orderMapper.selectStatusByOrderId(orderId);
            if (orderStatus == null) {
                return "fail";
            }
            if (Order.OrderStatus.PAID.equals(orderStatus)) {
                return "success";
            }
            if (!Order.OrderStatus.UNPAID.equals(orderStatus)) {
                return "success";
            }

            BigDecimal paymentAmount = orderMapper.selectPaymentAmountByOrderId(orderId);
            if (paymentAmount == null || totalAmount == null || new BigDecimal(totalAmount).compareTo(paymentAmount) != 0) {
                return "fail";
            }

            List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(orderId);
            if (orderItemList.isEmpty()) {
                throw new IllegalStateException("Order item does not exist.");
            }

            Map<Long, Integer> productQuantityMap = orderItemList.stream()
                    .collect(Collectors.groupingBy(
                            OrderItem::getProductId,
                            Collectors.summingInt(OrderItem::getQuantity)
                    ));

            Result<Void> result = productPrivateClient.increaseSalesVolume(productQuantityMap);
            if (result == null || result.isError()) {
                throw new IllegalStateException("Failed to increase product sales volume.");
            }

            int affectedRows = orderMapper.updateStatusToPaidIfUnPaid(orderId);
            if (affectedRows == 0) {
                throw new IllegalStateException("Order status changed while processing pay notify.");
            }
        }

        return "success";
    }
}
