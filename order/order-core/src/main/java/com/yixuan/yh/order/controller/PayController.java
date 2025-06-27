package com.yixuan.yh.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.yixuan.yh.order.mapper.OrderMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import com.yixuan.yh.order.properties.AliPayProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/public/pay")
public class PayController {

    @Autowired
    private AliPayProperties aliPayProperties;
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/request-pay")
    public void requestpay(Long orderId, HttpServletResponse httpResponse) throws IOException, AlipayApiException {

        // 获取订单金额
        BigDecimal paymentAmount = orderMapper.selectPaymentAmountByOrderId(orderId);

        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperties.url, aliPayProperties.appId, aliPayProperties.appPrivateKey, "json", "UTF-8", aliPayProperties.alipayPublicKey,"RSA2");
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
        alipayRequest.setNotifyUrl(aliPayProperties.notifyUrl + "/order/api/public/pay/notify"); // 在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+ orderId +"\"," +
                "    \"total_amount\":"+ paymentAmount +"," +
                "    \"subject\":\""+ "YH商城" +"\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        httpResponse.setContentType("text/html;charset=UTF-8");
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
    }

    @PostMapping("/notify")
    public String payNotify(@RequestParam Map<String, String> params) throws AlipayApiException {
        // 验证签名
        boolean verifyResult = AlipaySignature.rsaCheckV1(
                params,
                aliPayProperties.getAlipayPublicKey(),
                "UTF-8",
                "RSA2"
        );

        if (!verifyResult) {
            return "fail";
        }

        // 获取关键参数
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String totalAmount = params.get("total_amount");
        String tradeStatus = params.get("trade_status");

        // 处理交易成功逻辑
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            orderMapper.updateStatusByOrderId(outTradeNo, Order.OrderStatus.PAID);
        }

        return "success";
    }

}
