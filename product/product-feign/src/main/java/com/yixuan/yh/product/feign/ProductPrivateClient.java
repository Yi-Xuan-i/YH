package com.yixuan.yh.product.feign;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.product.pojo.response.GetOnSaleProductForLiveResponse;
import com.yixuan.yh.product.pojo.response.PartOfCartOrderResponse;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "productService", contextId = "ProductPrivateClient")
public interface ProductPrivateClient {
    @GetMapping("/product/api/private/live/merchant-on-sale")
    Result<Boolean> isMerchantOnSaleProduct(@RequestParam Long merchantId, @RequestParam Long productId);

    @GetMapping("/product/api/private/live/products")
    Result<Map<Long, GetOnSaleProductForLiveResponse>> getProductsForLive(@RequestParam List<Long> productIdList);

    @GetMapping("/product/api/private/order-part")
    Result<PartOfOrderResponse> getPartOfOrder(@RequestParam Long orderId, @RequestParam Long productId, @RequestParam Long skuId, @RequestParam Integer quantity);

    @GetMapping("/product/api/private/order-part/cart")
    Result<Map<Long, PartOfCartOrderResponse>> getPartOfCartOrder(@RequestParam Long orderId, @RequestParam List<Long> cartItemIdList);

    @PutMapping("/product/api/private/sku/{skuId}/reserved-stock")
    Result<Void> putReservedStock(@PathVariable Long skuId, @RequestBody Map<String, Integer> quantityMap);

    @PutMapping("/product/api/private/sales-volume")
    Result<Void> increaseSalesVolume(@RequestBody Map<Long, Integer> productQuantityMap);
}
