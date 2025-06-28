package com.yixuan.yh.order.service.impl;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.order.mapper.OrderItemMapper;
import com.yixuan.yh.order.mapper.OrderMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import com.yixuan.yh.order.pojo.entity.OrderItem;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;
import com.yixuan.yh.order.properties.AliPayProperties;
import com.yixuan.yh.order.service.OrderService;
import com.yixuan.yh.product.feign.ProductPrivateClient;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AliPayProperties aliPayProperties;
    @Autowired
    private ProductPrivateClient productPrivateClient;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @GlobalTransactional
    public PostOrderResponse postOrder(Long userId, PostOrderRequest postOrderRequest) throws BadRequestException {

        Long orderId = snowflakeUtils.nextId();
        // 获取订单需要用到商品数据（并预占库存）
        Result<PartOfOrderResponse> result = productPrivateClient.getPartOfOrder(orderId, postOrderRequest.getProductId(), postOrderRequest.getSkuId(), postOrderRequest.getQuantity());
        if (result.isError()) {
            throw new BadRequestException(result.getMsg());
        }
        PartOfOrderResponse partOfOrderResponse = result.getData();

        // order
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setDeliveryAddress(postOrderRequest.getDeliveryAddress());
        order.setMerchantId(partOfOrderResponse.getMerchantId());
        order.setPaymentAmount(partOfOrderResponse.getPrice().multiply(new BigDecimal(postOrderRequest.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
        order.setCreatedTime(LocalDateTime.now());
        orderMapper.insert(order);

        // order-item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(orderId);
        orderItem.setOrderId(order.getOrderId());
        orderItem.setProductId(postOrderRequest.getProductId());
        orderItem.setSkuId(postOrderRequest.getSkuId());
        orderItem.setSku(postOrderRequest.getSku());
        orderItem.setQuantity(postOrderRequest.getQuantity());
        orderItem.setProductName(partOfOrderResponse.getProductName());
        orderItem.setPrice(partOfOrderResponse.getPrice());
        orderItemMapper.insert(orderItem);

        return new PostOrderResponse(orderItem.getOrderId(), aliPayProperties.notifyUrl + "/order/api/public/pay/request-pay?orderId=" + order.getOrderId());
    }

    @Override
    public Boolean getIsPaid(Long orderId) {
        return orderMapper.selectStatusByOrderId(orderId).equals(Order.OrderStatus.PAID);
    }

    @Override
    public Boolean putToCancelIfUnpaid(Long orderId) {
        return orderMapper.updateStatusToCancelIfUnPaid(orderId);
    }
}
