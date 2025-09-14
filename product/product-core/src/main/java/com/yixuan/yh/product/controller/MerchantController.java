package com.yixuan.yh.product.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.product.pojo.request.PostCarouselRequest;
import com.yixuan.yh.product.pojo.request.PostSkuSpecRequest;
import com.yixuan.yh.product.pojo.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.pojo.request.PutSkuRequest;
import com.yixuan.yh.product.pojo.response.ProductEditResponse;
import com.yixuan.yh.product.pojo.response.ProductManageItemResponse;
import com.yixuan.yh.product.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Merchant")
@RestController
@RequestMapping("/me/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Operation(summary = "获取自己店铺的商品")
    @GetMapping
    public Result<List<ProductManageItemResponse>> getMerchantProduct() {
        return Result.success(merchantService.getMerchantProduct(UserContext.getUser()));
    }

    @Operation(summary = "获取商品编辑信息")
    @GetMapping("/edit/{productId}")
    public Result<ProductEditResponse> getMerchantProductEditData(@PathVariable Long productId) {
        return Result.success(merchantService.getMerchantProductEditData(productId));
    }

    @Operation(summary = "新增店铺商品")
    @PostMapping
    public Result<Void> postMerchantProduct(@ModelAttribute PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        merchantService.postMerchantProduct(UserContext.getUser(), putProductBasicInfoRequest);
        return Result.success();
    }

    @Operation(summary = "修改店铺商品的基本信息")
    @PutMapping("/{productId}")
    public Result<Void> putMerchantProductBasicInfo(@PathVariable Long productId, @ModelAttribute PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        merchantService.putMerchantProductBasicInfo(productId, putProductBasicInfoRequest);
        return Result.success();
    }

    @Operation(summary = "删除店铺商品")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteMerchantProduct(@PathVariable Long productId) {
        merchantService.deleteMerchantProduct(productId);
        return Result.success();
    }

    @Operation(summary = "新增商品规格对")
    @PostMapping("/sku/spec")
    public Result<Void> postSkuSpec(@RequestBody PostSkuSpecRequest postSkuSpecRequest) {
        merchantService.postSkuSpec(postSkuSpecRequest);
        return Result.success();
    }

    @Operation(summary = "商品新增轮播图")
    @PostMapping("/carousel/{productId}")
    public Result<Long> postCarousel(@PathVariable Long productId, @ModelAttribute PostCarouselRequest postCarouselRequest) throws IOException {
        return Result.success(merchantService.postCarousel(UserContext.getUser(), productId, postCarouselRequest));
    }

    @Operation(summary = "商品新增轮播图")
    @DeleteMapping("/carousel/{productId}/{carouselId}")
    public Result<Void> deleteCarousel(@PathVariable Long productId, @PathVariable Long carouselId) throws BadRequestException {
        merchantService.deleteCarousel(UserContext.getUser(), productId, carouselId);
        return Result.success();
    }

    @Operation(summary = "删除商品规格对")
    @PutMapping("/sku")
    public Result<Void> putSku(@RequestBody PutSkuRequest putSkuRequest) throws BadRequestException {
        merchantService.putSku(UserContext.getUser(), putSkuRequest);
        return Result.success();
    }

}
