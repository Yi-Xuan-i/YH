package com.yixuan.yh.product.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.product.pojo.request.*;
import com.yixuan.yh.product.pojo.response.MerchantProductStatsResponse;
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

    @Operation(summary = "修改商品状态")
    @PutMapping("/status/{productId}")
    public Result<Void> putMerchantProductStatus(@PathVariable Long productId, @RequestBody PutProductStatusRequest putProductStatusRequest) {
        merchantService.putMerchantProductStatus(productId, putProductStatusRequest);
        return Result.success();
    }

    @Operation(summary = "获取自己店铺的商品（不区分状态）")
    @GetMapping
    public Result<List<ProductManageItemResponse>> getMerchantProduct() {
        return Result.success(merchantService.getMerchantProduct(UserContext.getUser()));
    }

    @Operation(summary = "获取自己店铺的商品统计数据")
    @GetMapping("/stats")
    public Result<MerchantProductStatsResponse> getMerchantProductStats() {
        return Result.success(merchantService.getMerchantProductStats(UserContext.getUser()));
    }

    @Operation(summary = "获取自己店铺的销售中商品（用于直播间展示商品列表能够让主播挑选上品上架到直播间）")
    @GetMapping("/on-sale")
    public Result<List<ProductManageItemResponse>> geOnSaleProductForLive() {
        return Result.success(merchantService.getMerchantOnSaleProduct(UserContext.getUser()));
    }

    @Operation(summary = "获取商品编辑信息")
    @GetMapping("/edit/{productId}")
    public Result<ProductEditResponse> getMerchantProductEditData(@PathVariable Long productId) {
        return Result.success(merchantService.getMerchantProductEditData(productId));
    }

    @Operation(summary = "获取上传视频的预签名URL（编辑器）")
    @GetMapping("/edit/upload-video/presigned-url")
    public Result<String> getEditUploadVideoPresignedUrl() {
        return Result.success(merchantService.getEditUploadVideoPresignedUrl(UserContext.getUser()));
    }

    @Operation(summary = "获取上传图片的预签名URL（编辑器）")
    @GetMapping("/edit/upload-image/presigned-url")
    public Result<String> getEditUploadImagePresignedUrl() {
        return Result.success(merchantService.getEditUploadImagePresignedUrl(UserContext.getUser()));
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

    @Operation(summary = "设置主规格")
    @PutMapping("/sku/main/{productId}")
    public Result<Void> putSkuMain(@PathVariable Long productId, @RequestBody PutSkuMainRequest putSkuMainRequest) {
        merchantService.putSkuMain(productId, putSkuMainRequest);
        return Result.success();
    }

    @Operation(summary = "删除商品规格对")
    @PutMapping("/sku")
    public Result<Void> putSku(@RequestBody PutSkuRequest putSkuRequest) throws BadRequestException {
        merchantService.putSku(UserContext.getUser(), putSkuRequest);
        return Result.success();
    }

    @Operation(summary = "SKU新增轮播图")
    @PostMapping("/sku/carousel/{skuId}")
    public Result<Void> postSkuCarousel(@PathVariable Long skuId, @ModelAttribute PostSkuCarouselRequest postSkuCarouselRequest) throws IOException {
        merchantService.postSkuCarousel(skuId, postSkuCarouselRequest);
        return Result.success();
    }

    @Operation(summary = "SKU删除轮播图")
    @DeleteMapping("/sku/carousel/{carouselId}")
    public Result<Void> deleteSkuCarousel(@PathVariable Long carouselId) {
        merchantService.deleteSkuCarousel(carouselId);
        return Result.success();
    }

    @Operation(summary = "根据SKU获取轮播图URL列表")
    @GetMapping("/sku/carousels/{skuId}")
    public Result<List<String>> getSkuCarousels(@PathVariable Long skuId) {
        return Result.success(merchantService.getSkuCarousels(skuId));
    }
}
