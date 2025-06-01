package com.yixuan.yh.product.controller;

import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import com.yixuan.yh.product.request.PostSkuSpecRequest;
import com.yixuan.yh.product.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.response.ProductEditResponse;
import com.yixuan.yh.product.response.ProductManageItemResponse;
import com.yixuan.yh.product.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public Result<List<ProductManageItemResponse>> getMerchantProduct() {
        return Result.success(merchantService.getMerchantProduct(UserContext.getUser()));
    }

    @GetMapping("/edit/{productId}")
    public Result<ProductEditResponse> getMerchantProductEditData(@PathVariable Long productId) {
        return Result.success(merchantService.getMerchantProductEditData(productId));
    }

    @PostMapping
    public Result<Void> postMerchantProduct(@ModelAttribute PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        merchantService.postMerchantProduct(UserContext.getUser(), putProductBasicInfoRequest);
        return Result.success();
    }

    @PutMapping("/{productId}")
    public Result<Void> putMerchantProductBasicInfo(@PathVariable Long productId, @ModelAttribute PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        merchantService.putMerchantProductBasicInfo(productId, putProductBasicInfoRequest);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> deleteMerchantProduct(@PathVariable Long productId) {
        merchantService.deleteMerchantProduct(productId);
        return Result.success();
    }

    @PostMapping("/sku/spec")
    public Result<Void> postSkuSpec(@RequestBody PostSkuSpecRequest postSkuSpecRequest) {
        merchantService.postSkuSpec(postSkuSpecRequest);
        return Result.success();
    }

}
