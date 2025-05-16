package com.yixuan.yh.admin.utils;

import com.yixuan.yh.admin.service.PermissionService;
import com.yixuan.yh.commom.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermissionChecker {

    @Autowired
    private PermissionService permissionService;

    public boolean checkPermissions(String[] requiredPermissions) {
        Set<String> userPermissions = permissionService.getAdminPermissions(UserContext.getUser());

        // 检查用户是否包含所有需要的权限
        for (String required : requiredPermissions) {
            if (!userPermissions.contains(required)) {
                return false;
            }
        }
        return true;
    }
}