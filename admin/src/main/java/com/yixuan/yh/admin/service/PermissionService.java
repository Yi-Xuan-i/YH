package com.yixuan.yh.admin.service;

import java.util.Set;

public interface PermissionService {
    Set<String> getAdminPermissions(Long id);

    Set<String> getAllPermissions();
}
