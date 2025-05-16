package com.yixuan.yh.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminPermissionMapper {
    void insertAdminPermissions(Long adminId, List<String> permissionList);
}
