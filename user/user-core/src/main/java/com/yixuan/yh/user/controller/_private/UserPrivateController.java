package com.yixuan.yh.user.controller._private;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private")
public class UserPrivateController {

    @Autowired
    private UserService userService;

    @GetMapping("/name")
    public Result<String> getName(@RequestParam String id) {
        return Result.success(userService.getName(id));
    }

}
