package com.yixuan.yh.product.controller._public;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import com.yixuan.yh.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "ProductPublic")
@RestController
@RequestMapping("/public")
public class ProductPublicController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "（随机/推荐）获取商品")
    @GetMapping
    public Result<List<ProductSummaryResponse>> getProducts() {
        return Result.success(productService.getProducts());
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/detail/{productId}")
    public Result<ProductDetailResponse> getDetailProducts(@PathVariable Long productId) {
        return Result.success(productService.getDetailProducts(productId));
    }
}
