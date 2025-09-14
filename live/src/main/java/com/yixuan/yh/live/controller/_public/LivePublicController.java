package com.yixuan.yh.live.controller._public;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.live.response.GetLiveResponse;
import com.yixuan.yh.live.service.LiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Tag(name = "LivePublic")
@RestController
@RequestMapping("/public")
public class LivePublicController {

    @Autowired
    private LiveService liveService;

    @Operation(summary = "srs推流回调")
    @PostMapping("/publish")
    public int publishCallback(@RequestBody Map<String, String> body) throws BadRequestException {
        MultiValueMap<String, String> params = UriComponentsBuilder
                .fromUriString(body.get("param"))
                .build()
                .getQueryParams();
        liveService.publishCallback(Long.parseLong(body.get("stream")), body.get("client_id"), params.getFirst("token"));
        return 0;
    }

    @Operation(summary = "srs结束推流回调")
    @PostMapping("/unpublish")
    public int unpublishCallback(@RequestBody Map<String, String> body) throws BadRequestException {
        liveService.unpublishCallback(Long.parseLong(body.get("stream")), body.get("client_id"));
        return 0;
    }

    @Operation(summary = "搜索直播")
    @GetMapping("/search")
    public Result<List<GetLiveResponse>> getLivesByQuery(@RequestParam String query) {
        return Result.success(liveService.getLivesByQuery(query));
    }

}
