package com.yixuan.yh.live.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.LiveProductItemResponse;
import com.yixuan.yh.live.service.LiveProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "LiveProduct")
@RestController
@RequestMapping("/me/product")
public class LiveProductController {

    @Autowired
    private LiveProductService liveProductService;

    @Operation(summary = "直播上架商家商品")
    @PostMapping("/merchant")
    public Result<Void> postMerchantProduct(@RequestBody PostLiveProductRequest postLiveProductRequest) {
        liveProductService.postMerchantProduct(UserContext.getUser(), postLiveProductRequest);
        return Result.success();
    }

    @Operation(summary = "获取直播上架的商品")
    @GetMapping("/room/{roomId}")
    public Result<List<LiveProductItemResponse>> getRoomLiveProduct(@PathVariable Long roomId) {
        return Result.success(liveProductService.getRoomLiveProduct(roomId));
    }

    @Operation(summary = "获取直播上架的商品详情")
    @GetMapping("/{id}")
    public Result<LiveProductItemResponse> getLiveProduct(@PathVariable Long id) {
        return Result.success(liveProductService.getLiveProduct(id));
    }

}
