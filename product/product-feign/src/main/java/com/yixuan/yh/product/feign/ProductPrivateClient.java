package com.yixuan.yh.product.feign;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "productService", contextId = "ProductPrivateClient")
public interface ProductPrivateClient {
     @GetMapping("/product/api/private/order-part")
     Result<PartOfOrderResponse> getPartOfOrder(@RequestParam Long orderId, @RequestParam Long productId, @RequestParam Long skuId, @RequestParam Integer quantity);

     @PutMapping("/product/api/private/sku/{skuId}/reserved-stock")
     Result<Void> putReservedStock(@PathVariable Long skuId, @RequestBody Map<String, Integer> quantityMap);
}