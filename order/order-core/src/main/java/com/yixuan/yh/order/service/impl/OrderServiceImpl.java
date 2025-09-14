package com.yixuan.yh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
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
    @Autowired
    private AlipayClient alipayClient;

    @Override
    @GlobalTransactional
    public PostOrderResponse postOrder(Long userId, PostOrderRequest postOrderRequest) throws BadRequestException, AlipayApiException {

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

        /* 获取请求二维码 */
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(aliPayProperties.getNotifyUrl() + "/order/api/public/pay/notify");

        // 构建订单参数（必须与本地订单一致）
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(String.valueOf(orderId));
        model.setTotalAmount(String.valueOf(order.getPaymentAmount()));
        model.setSubject("YH");
        model.setTimeoutExpress("1m");

        request.setBizModel(model);

        // 发起支付请求获取二维码
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            return new PostOrderResponse(orderId, response.getQrCode());
        } else {
            throw new BadRequestException("服务器异常！");
        }
    }

    @Override
    public Boolean getIsPaid(Long orderId) {
        return orderMapper.selectStatusByOrderId(orderId).equals(Order.OrderStatus.PAID);
    }

    @Override
    public Boolean putToCancelIfUnpaid(Long orderId) throws AlipayApiException {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            return orderMapper.updateStatusToCancelIfUnPaid(orderId);
        }
        // 订单已支付或处于无法关闭的状态
        if ("ACQ.TRADE_STATUS_ERROR".equals(response.getSubCode())) {
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            queryRequest.setBizContent("{\"out_trade_no\":\"" + orderId + "\"}");
            AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
            // 确认是因为已支付导致的关闭失败
            return !"TRADE_SUCCESS".equals(queryResponse.getTradeStatus());
        }
        // 交易不存在（发起了订单但实际上没有发送请求进行支付）
        return "ACQ.TRADE_NOT_EXIST".equals(response.getSubCode());
    }
}
