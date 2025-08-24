package com.yixuan.yh.video.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import com.yixuan.yh.video.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "InteractionPrivate")
@RestController
@RequestMapping("/private/interaction")
public class InteractionPrivateController {

    @Autowired
    private InteractionService interactionService;

    @Operation(summary = "批量新增点赞数和点赞关系")
    @PostMapping("/like-batch")
    public Result<Void> likeBatch(@RequestBody VideoInteractionBatchRequest videoInteractionBatchRequest) {
        interactionService.likeBatch(videoInteractionBatchRequest);
        return Result.success();
    }

    @Operation(summary = "批量新增收藏数和收藏关系")
    @PostMapping("/favorite-batch")
    public Result<Void> favoriteBatch(@RequestBody VideoInteractionBatchRequest videoInteractionBatchRequest) {
        interactionService.favoriteBatch(videoInteractionBatchRequest);
        return Result.success();
    }

}
