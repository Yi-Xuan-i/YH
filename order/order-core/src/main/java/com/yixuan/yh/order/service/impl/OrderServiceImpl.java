package com.yixuan.yh.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yixuan.yh.common.exception.YHServerException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.order.mapper.OrderItemMapper;
import com.yixuan.yh.order.mapper.OrderMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import com.yixuan.yh.order.pojo.entity.OrderItem;
import com.yixuan.yh.order.pojo.request.PostCartOrderRequest;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import com.yixuan.yh.order.pojo.response.PendingPaymentOrderResponse;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;
import com.yixuan.yh.order.properties.AliPayProperties;
import com.yixuan.yh.order.service.OrderService;
import com.yixuan.yh.product.feign.ProductPrivateClient;
import com.yixuan.yh.product.pojo.response.PartOfCartOrderResponse;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AliPayProperties aliPayProperties;
    @Autowired
    private ProductPrivateClient productPrivateClient;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private AlipayClient alipayClient;

    @Override
    @GlobalTransactional
    public PostOrderResponse postOrder(Long userId, PostOrderRequest postOrderRequest) throws AlipayApiException {

        Long orderId = IdWorker.getId();
        // 获取订单需要用到商品数据（并预占库存）
        Result<PartOfOrderResponse> result = productPrivateClient.getPartOfOrder(orderId, postOrderRequest.getProductId(), postOrderRequest.getSkuId(), postOrderRequest.getQuantity());
        PartOfOrderResponse partOfOrderResponse = result.getData();

        // order
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setDeliveryAddress(postOrderRequest.getDeliveryAddress());
        order.setMerchantId(partOfOrderResponse.getMerchantId());
        order.setPaymentAmount(partOfOrderResponse.getPrice().multiply(new BigDecimal(postOrderRequest.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
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
        model.setQrCodeTimeoutExpress("1m");

        request.setBizModel(model);

        // 发起支付请求获取二维码
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            return new PostOrderResponse(orderId, response.getQrCode());
        } else {
            throw new YHServerException("获取支付二维码失败！");
        }
    }

    @Override
    public PostOrderResponse postCartOrder(Long userId, PostCartOrderRequest postCartOrderRequest) throws AlipayApiException {
        Long orderId = IdWorker.getId();
        // 获取订单需要用到商品数据（并预占库存）
        Result<Map<Long, PartOfCartOrderResponse>> result = productPrivateClient.getPartOfCartOrder(orderId, postCartOrderRequest.getCartItemIdList());
        Map<Long, PartOfCartOrderResponse> partOfCartOrderResponseMap = result.getData();

        // order
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setDeliveryAddress(postCartOrderRequest.getDeliveryAddress());
        order.setMerchantId(partOfCartOrderResponseMap.values().iterator().next().getMerchantId());
        order.setPaymentAmount(partOfCartOrderResponseMap.values().stream().map(r -> r.getPrice().multiply(new BigDecimal(r.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        orderMapper.insert(order);

        // order-item
        partOfCartOrderResponseMap.forEach((cartItemId, partOfCartOrderResponse) -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(IdWorker.getId());
            orderItem.setOrderId(order.getOrderId());
            orderItem.setProductId(partOfCartOrderResponse.getProductId());
            orderItem.setSkuId(partOfCartOrderResponse.getSkuId());
            orderItem.setSku(partOfCartOrderResponse.getSku());
            orderItem.setQuantity(partOfCartOrderResponse.getQuantity());
            orderItem.setProductName(partOfCartOrderResponse.getProductName());
            orderItem.setPrice(partOfCartOrderResponse.getPrice());
            orderItemMapper.insert(orderItem);
        });

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
            throw new YHServerException("获取支付二维码失败！");
        }
    }

    @Override
    public List<PendingPaymentOrderResponse> getPendingPaymentOrders(Long userId) {
        return orderMapper.selectPendingPaymentByUserId(userId).stream()
                .map(order -> new PendingPaymentOrderResponse(
                        order.getOrderId(),
                        order.getMerchantId(),
                        order.getPaymentAmount(),
                        order.getOrderStatus(),
                        order.getDeliveryAddress(),
                        order.getCreatedAt(),
                        orderItemMapper.selectByOrderId(order.getOrderId()).stream()
                                .map(orderItem -> new PendingPaymentOrderResponse.OrderItemResponse(
                                        orderItem.getOrderItemId(),
                                        orderItem.getProductId(),
                                        orderItem.getSkuId(),
                                        orderItem.getSku(),
                                        orderItem.getProductName(),
                                        orderItem.getQuantity(),
                                        orderItem.getPrice()))
                                .toList()))
                .toList();
    }

    @Override
    public Boolean getIsPaid(Long orderId) {
        return orderMapper.selectStatusByOrderId(orderId).equals(Order.OrderStatus.PAID);
    }

    @Override
    public Boolean putToCancelIfUnpaid(Long orderId) throws AlipayApiException {
       // 查询交易状态
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        queryRequest.setBizContent("{\"out_trade_no\":\"" + orderId + "\"}");
        AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
        String tradeStatus = queryResponse.getTradeStatus();
        System.out.println(tradeStatus);

        return true;
//        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
//        JSONObject bizContent = new JSONObject();
//        bizContent.put("out_trade_no", orderId);
//        request.setBizContent(bizContent.toString());
//        AlipayTradeCloseResponse response = alipayClient.execute(request);
//        if (response.isSuccess()) {
//            return orderMapper.updateStatusToCancelIfUnPaid(orderId);
//        }
//        // 订单已支付或处于无法关闭的状态
//        if ("ACQ.TRADE_STATUS_ERROR".equals(response.getSubCode())) {
//            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
//            queryRequest.setBizContent("{\"out_trade_no\":\"" + orderId + "\"}");
//            AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
//            System.out.println(queryResponse.getTradeStatus());
//            // 确认是因为已支付导致的关闭失败
//            return !"TRADE_SUCCESS".equals(queryResponse.getTradeStatus());
//        }
//        // 交易不存在（发起了订单但实际上没有发送请求进行支付）
//        return "ACQ.TRADE_NOT_EXIST".equals(response.getSubCode());
    }

    @Override
    public MerchantDailySalesResponse getMerchantDailySales(List<Long> productIdList) {
        MerchantDailySalesResponse emptyResponse = new MerchantDailySalesResponse();
        emptyResponse.setDailySalesVolume(0);
        emptyResponse.setDailySalesAmount(BigDecimal.ZERO);
        if (productIdList == null || productIdList.isEmpty()) {
            return emptyResponse;
        }

        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);
        MerchantDailySalesResponse response = orderItemMapper.selectMerchantDailySales(productIdList, startTime, endTime);
        if (response == null) {
            return emptyResponse;
        }
        if (response.getDailySalesVolume() == null) {
            response.setDailySalesVolume(0);
        }
        if (response.getDailySalesAmount() == null) {
            response.setDailySalesAmount(BigDecimal.ZERO);
        }
        return response;
    }
}
