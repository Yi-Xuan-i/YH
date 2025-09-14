package com.yixuan.yh.order.controller;

import com.alipay.api.AlipayApiException;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;
import com.yixuan.yh.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order")
@RestController
@RequestMapping("/me")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "生成订单")
    @PostMapping
    public Result<PostOrderResponse> postOrder(@RequestBody PostOrderRequest postOrderRequest) throws BadRequestException, AlipayApiException {
        return Result.success(orderService.postOrder(UserContext.getUser(), postOrderRequest));
    }

    @Operation(summary = "获取订单支付状态")
    @GetMapping("/pay-status/{orderId}")
    public Result<Boolean> getIsPaid(@PathVariable Long orderId) {
        return Result.success(orderService.getIsPaid(orderId));
    }
}
