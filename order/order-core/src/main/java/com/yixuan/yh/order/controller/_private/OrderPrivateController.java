package com.yixuan.yh.order.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import com.yixuan.yh.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "OrderPrivate")
@RestController
@RequestMapping("/private")
public class OrderPrivateController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "get merchant daily sales")
    @PostMapping("/merchant/daily-sales")
    public Result<MerchantDailySalesResponse> getMerchantDailySales(@RequestBody List<Long> productIdList) {
        return Result.success(orderService.getMerchantDailySales(productIdList));
    }
}
