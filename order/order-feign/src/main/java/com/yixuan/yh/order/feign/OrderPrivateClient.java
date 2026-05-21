package com.yixuan.yh.order.feign;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.order.pojo.response.MerchantDailySalesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "orderService", contextId = "OrderPrivateClient")
public interface OrderPrivateClient {
    @PostMapping("/order/api/private/merchant/daily-sales")
    Result<MerchantDailySalesResponse> getMerchantDailySales(@RequestBody List<Long> productIdList);
}
