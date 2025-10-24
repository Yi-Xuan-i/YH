package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.request.PostCollectionsRequest;
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
    public Result<List<GetCollectionsResponse>> getCollections() {
        return Result.success(collectionsService.getCollections(UserContext.getUser()));
    }

    @Operation(summary = "创建收藏夹")
    @PostMapping
    public Result<String> postCollections(@RequestBody PostCollectionsRequest postCollectionsRequest) {
        return Result.success(collectionsService.postCollections(UserContext.getUser(), postCollectionsRequest));
    }

}