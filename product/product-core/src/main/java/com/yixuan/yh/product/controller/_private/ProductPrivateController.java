package com.yixuan.yh.product.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.product.pojo.response.GetOnSaleProductForLiveResponse;
import com.yixuan.yh.product.pojo.response.PartOfCartOrderResponse;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import com.yixuan.yh.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "ProductPrivate")
@RestController
@RequestMapping("/private")
public class ProductPrivateController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "校验商家直播上架商品")
    @GetMapping("/live/merchant-on-sale")
    public Result<Boolean> isMerchantOnSaleProduct(@RequestParam Long merchantId, @RequestParam Long productId) {
        return Result.success(productService.isMerchantOnSaleProduct(merchantId, productId));
    }

    @Operation(summary = "获取直播商品数据")
    @GetMapping("/live/products")
    public Result<Map<Long, GetOnSaleProductForLiveResponse>> getProductsForLive(@RequestParam List<Long> productIdList) {
        return Result.success(productService.getProductsForLive(productIdList));
    }

    @Operation(summary = "获取商品数据（用于生成订单）")
    @GetMapping("/order-part")
    public Result<PartOfOrderResponse> getPartOfOrder(@RequestParam Long orderId, @RequestParam Long productId, @RequestParam Long skuId, @RequestParam Integer quantity) throws BadRequestException, InterruptedException {
        return Result.success(productService.getPartOfOrder(orderId, productId, skuId, quantity));
    }

    @Operation(summary = "获取商品数据（用于生成购物车订单）")
    @GetMapping("/order-part/cart")
    public Result<Map<Long, PartOfCartOrderResponse>> getPartOfCartOrder(@RequestParam Long orderId, @RequestParam List<Long> cartItemIdList) {
        return Result.success(productService.getPartOfCartOrder(orderId, cartItemIdList));
    }

    @Operation(summary = "预占库存")
    @PutMapping("/sku/{skuId}/reserved-stock")
    public Result<Void> putReservedStock(@PathVariable Long skuId, @RequestBody Map<String, Integer> quantityMap) {
        productService.putReservedStock(skuId, quantityMap.get("quantity"));
        return Result.success();
    }

    @Operation(summary = "增加销量")
    @PutMapping("/sales-volume")
    public Result<Void> increaseSalesVolume(@RequestBody Map<Long, Integer> productQuantityMap) {
        productService.increaseSalesVolume(productQuantityMap);
        return Result.success();
    }
}
