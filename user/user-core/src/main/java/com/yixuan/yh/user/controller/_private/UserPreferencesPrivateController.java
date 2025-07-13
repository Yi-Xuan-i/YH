package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/preferences")
public class UserPreferencesPrivateController {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @GetMapping("/video/{userId}")
    public Result<float[]> getUserVideoPreferences(@PathVariable Long userId) {
        return Result.success(userPreferencesService.getUserVideoPreferences(userId));
    }

}
