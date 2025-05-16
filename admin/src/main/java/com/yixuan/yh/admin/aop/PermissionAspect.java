package com.yixuan.yh.admin.aop;

import com.yixuan.yh.admin.annotations.RequiresPermission;
import com.yixuan.yh.admin.utils.PermissionChecker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionChecker permissionChecker;

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        String[] requiredPermissions = requiresPermission.value();

        if (!permissionChecker.checkPermissions(requiredPermissions)) {
            throw new RuntimeException("你的权限不足！");
        }

        return joinPoint.proceed();
    }
}