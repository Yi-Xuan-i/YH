package com.yixuan.yh.admin.service.impl;

import com.yixuan.yh.admin.init.PermissionInitializer;
import com.yixuan.yh.admin.mapper.PermissionMapper;
import com.yixuan.yh.admin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    @Lazy
    private PermissionInitializer permissionInitializer;

    @Override
    public Set<String> getAdminPermissions(Long id) {
        return permissionMapper.selectAdminPermissions(id);
    }

    @Override
    public Set<String> getAllPermissions() {
        return permissionInitializer.getAllPermissionValues();
    }
}
