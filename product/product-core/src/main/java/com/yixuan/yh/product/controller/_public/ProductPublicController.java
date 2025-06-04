package com.yixuan.yh.product.controller._public;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.product.pojo.response.ProductDetailResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import com.yixuan.yh.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
public class ProductPublicController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<List<ProductSummaryResponse>> getProducts() {
        return Result.success(productService.getProducts());
    }

    @GetMapping("/detail/{productId}")
    public Result<ProductDetailResponse> getDetailProducts(@PathVariable Long productId) {
        return Result.success(productService.getDetailProducts(productId));
    }
}
