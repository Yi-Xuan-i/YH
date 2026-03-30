package com.yixuan.yh.video.controller;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.video.pojo.request.PostCommentRequest;
import com.yixuan.yh.video.pojo.response.GetDirectCommentResponse;
import com.yixuan.yh.video.pojo.response.GetReplyCommentResponse;
import com.yixuan.yh.video.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Interaction")
@RestController
@RequestMapping("/me/interaction")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @Operation(summary = "点赞")
    @PostMapping("/like/{videoId}")
    public Result<Void> like(@PathVariable Long videoId) throws Exception {
        interactionService.like(UserContext.getUser(), videoId);
        return Result.success();
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/like/{videoId}")
    public Result<Void> unlike(@PathVariable Long videoId) throws Exception {
        interactionService.unlike(UserContext.getUser(), videoId);
        return Result.success();
    }

    @Operation(summary = "收藏")
    @PostMapping("/favorite/{videoId}")
    public Result<Void> favorite(@PathVariable Long videoId) throws Exception {
        interactionService.favorite(UserContext.getUser(), videoId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{videoId}")
    public Result<Void> unfavorite(@PathVariable Long videoId) throws Exception {
        interactionService.unfavorite(UserContext.getUser(), videoId);
        return Result.success();
    }

    @Operation(summary = "视频评论（或回复评论）")
    @PostMapping("/comment/{videoId}")
    public Result<String> comment(@PathVariable Long videoId, @RequestBody PostCommentRequest postCommentRequest) throws BadRequestException {
        return Result.success(interactionService.comment(videoId, UserContext.getUser(), postCommentRequest));
    }
}
