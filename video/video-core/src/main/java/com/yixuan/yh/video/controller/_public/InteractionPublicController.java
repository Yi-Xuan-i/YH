package com.yixuan.yh.video.controller._public;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.video.pojo.response.GetDirectCommentResponse;
import com.yixuan.yh.video.pojo.response.GetReplyCommentResponse;
import com.yixuan.yh.video.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Interaction")
@RestController
@RequestMapping("/public/interaction")
@RequiredArgsConstructor
public class InteractionPublicController {

    private final InteractionService interactionService;

    @Operation(summary = "获取视频直接评论")
    @GetMapping("/comment/{videoId}")
    public Result<List<GetDirectCommentResponse>> directComment(@PathVariable Long videoId, @RequestParam(required = false) Long lastMinId) {
        return Result.success(interactionService.directComment(videoId, lastMinId));
    }

    @Operation(summary = "获取视频评论的回复")
    @GetMapping("/comment/reply/{commentId}")
    public Result<List<GetReplyCommentResponse>> replyComment(@PathVariable Long commentId, @RequestParam(required = false) Long lastMaxId) {
        return Result.success(interactionService.replyComment(commentId, lastMaxId));
    }
}
