package com.yixuan.yh.admin.controller;

import com.yixuan.yh.admin.service.PermissionService;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Permission")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "获取管理员的权限")
    @GetMapping
    public Result<Set<String>> getAdminPermissions() {
        return Result.success(permissionService.getAdminPermissions(UserContext.getUser()));
    }

    @Operation(summary = "获取管理员权限列表")
    @GetMapping("/list")
    public Result<Set<String>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }
}
