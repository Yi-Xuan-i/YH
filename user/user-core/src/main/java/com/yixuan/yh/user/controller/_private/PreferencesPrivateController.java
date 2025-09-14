package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.service.PreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Preferences")
@RestController
@RequestMapping("/private/preferences")
public class PreferencesPrivateController {

    @Autowired
    private PreferencesService preferencesService;

    @Operation(summary = "获取用户“视频偏好向量”")
    @GetMapping("/video/{userId}")
    public Result<float[]> getUserVideoPreferences(@PathVariable Long userId) {
        return Result.success(preferencesService.getUserVideoPreferences(userId));
    }

}
