package com.yixuan.yh.order.service;

import com.alipay.api.AlipayApiException;
import com.yixuan.yh.order.pojo.request.PostCartOrderRequest;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import com.yixuan.yh.order.pojo.response.PendingPaymentOrderResponse;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;

import java.util.List;

public interface OrderService {
    PostOrderResponse postOrder(Long userId, PostOrderRequest postOrderRequest) throws AlipayApiException;

    PostOrderResponse postCartOrder(Long userId, PostCartOrderRequest postCartOrderRequest) throws AlipayApiException;

    List<PendingPaymentOrderResponse> getPendingPaymentOrders(Long userId);

    Boolean getIsPaid(Long orderId);

    Boolean putToCancelIfUnpaid(Long orderId) throws AlipayApiException;

    MerchantDailySalesResponse getMerchantDailySales(List<Long> productIdList);
}
