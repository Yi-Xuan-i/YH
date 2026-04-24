package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.response.GetCollectionsItemResponse;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
import com.yixuan.yh.video.pojo.request.PutCollectionsRequest;
import com.yixuan.yh.video.pojo.response.GetCollectionsResponse;
import com.yixuan.yh.video.service.CollectionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Collections")
@RestController
@RequestMapping("/me/collections")
@RequiredArgsConstructor
public class CollectionsController {

    private final CollectionsService collectionsService;

    @Operation(summary = "获取收藏夹")
    @GetMapping
    public Result<List<GetCollectionsResponse>> getCollections(@RequestParam(required = false) Long lastMinId) {
        return Result.success(collectionsService.getCollections(UserContext.getUser(), lastMinId));
    }

    @Operation(summary = "获取收藏夹中的视频")
    @GetMapping("/item/{collectionsId}")
    public Result<List<GetCollectionsItemResponse>> getCollectionsItemList(@PathVariable Long collectionsId, @RequestParam(required = false) Long lastMinId) {
        return Result.success(collectionsService.getCollectionsItemList(UserContext.getUser(), collectionsId, lastMinId));
    }

    @Operation(summary = "创建收藏夹")
    @PostMapping
    public Result<String> postCollections(@RequestBody PostCollectionsRequest postCollectionsRequest) {
        return Result.success(collectionsService.postCollections(UserContext.getUser(), postCollectionsRequest));
    }

    @Operation(summary = "修改收藏夹")
    @PutMapping("/{collectionsId}")
    public Result<Void> putCollections(@PathVariable Long collectionsId, @RequestBody PutCollectionsRequest putCollectionsRequest) {
        collectionsService.putCollections(collectionsId, putCollectionsRequest);
        return Result.success();
    }

    @Operation(summary = "删除收藏夹")
    @DeleteMapping("/{collectionsId}")
    public Result<Void> deleteCollections(@PathVariable Long collectionsId) {
        collectionsService.deleteCollections(UserContext.getUser(), collectionsId);
        return Result.success();
    }

}