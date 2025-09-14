package com.yixuan.yh.live.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.GetLiveProductResponse;
import com.yixuan.yh.live.response.PostLiveProductResponse;
import com.yixuan.yh.live.service.LiveProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "LiveProduct")
@RestController
@RequestMapping("/me/product")
public class LiveProductController {

    @Autowired
    private LiveProductService liveProductService;

    @Operation(summary = "直播上架商品")
    @PostMapping
    public Result<PostLiveProductResponse> postLiveProduct(@ModelAttribute PostLiveProductRequest postLiveProductRequest) throws IOException {
        return Result.success(liveProductService.postLiveProduct(UserContext.getUser(), postLiveProductRequest));
    }

    @Operation(summary = "获取直播上架的商品（当用户刚刚进入直播时调用）")
    @GetMapping("/room/{roomId}")
    public Result<List<GetLiveProductResponse>> getRoomLiveProduct(@PathVariable Long roomId) {
        return Result.success(liveProductService.getRoomLiveProduct(roomId));
    }

    @Operation(summary = "获取直播上架的商品的详情")
    @GetMapping("/{id}")
    public Result<GetLiveProductResponse> getLiveProduct(@PathVariable Long id) {
        return Result.success(liveProductService.getLiveProduct(id));
    }

}
