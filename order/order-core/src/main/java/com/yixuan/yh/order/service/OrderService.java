package com.yixuan.yh.order.service;

import com.alipay.api.AlipayApiException;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;
import org.apache.coyote.BadRequestException;

public interface OrderService {
    PostOrderResponse postOrder(Long userId, PostOrderRequest postOrderRequest) throws BadRequestException, AlipayApiException;

    Boolean getIsPaid(Long orderId);

    Boolean putToCancelIfUnpaid(Long orderId) throws AlipayApiException;
}
