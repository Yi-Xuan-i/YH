package com.yixuan.yh.admin.controller;

import com.yixuan.yh.admin.service.PermissionService;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public Result<Set<String>> getAdminPermissions() {
        return Result.success(permissionService.getAdminPermissions(UserContext.getUser()));
    }

    @GetMapping("/list")
    public Result<Set<String>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }
}
