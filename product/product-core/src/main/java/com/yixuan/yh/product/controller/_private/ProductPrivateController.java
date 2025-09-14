package com.yixuan.yh.product.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.product.pojo.response.PartOfOrderResponse;
import com.yixuan.yh.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "ProductPrivate")
@RestController
@RequestMapping("/private")
public class ProductPrivateController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取商品数据（用于生成订单）")
    @GetMapping("/order-part")
    public Result<PartOfOrderResponse> getPartOfOrder(@RequestParam Long orderId, @RequestParam Long productId, @RequestParam Long skuId, @RequestParam Integer quantity) throws BadRequestException, InterruptedException {
        return Result.success(productService.getPartOfOrder(orderId, productId, skuId, quantity));
    }

    @Operation(summary = "预占库存")
    @PutMapping("/sku/{skuId}/reserved-stock")
    public Result<Void> putReservedStock(@PathVariable Long skuId, @RequestBody Map<String, Integer> quantityMap) {
        productService.putReservedStock(skuId, quantityMap.get("quantity"));
        return Result.success();
    }
}
