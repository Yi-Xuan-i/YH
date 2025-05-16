package com.yixuan.yh.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@Mapper
public interface PermissionMapper {
    @Select("select permission_code from admin_permission where admin_id = #{adminId}")
    Set<String> selectAdminPermissions(Long adminId);
}
