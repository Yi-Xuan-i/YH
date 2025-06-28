package com.yixuan.yh.order.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.order.pojo.request.PostOrderRequest;
import com.yixuan.yh.order.pojo.response.PostOrderResponse;
import com.yixuan.yh.order.service.OrderService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<PostOrderResponse> postOrder(@RequestBody PostOrderRequest postOrderRequest) throws BadRequestException {
        return Result.success(orderService.postOrder(UserContext.getUser(), postOrderRequest));
    }

    @GetMapping("/pay-status/{orderId}")
    public Result<Boolean> getIsPaid(@PathVariable Long orderId) {
        return Result.success(orderService.getIsPaid(orderId));
    }
}
