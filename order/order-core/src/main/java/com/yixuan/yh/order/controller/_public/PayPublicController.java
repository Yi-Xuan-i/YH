package com.yixuan.yh.order.controller._public;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.yixuan.yh.order.mapper.OrderMapper;
import com.yixuan.yh.order.pojo.entity.Order;
import com.yixuan.yh.order.properties.AliPayProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "PayPublic")
@RestController
@RequestMapping("/public/pay")
public class PayPublicController {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AliPayProperties aliPayProperties;
    @Autowired
    private OrderMapper orderMapper;

    @Operation(summary = "支付宝支付成功回调")
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
