package com.yixuan.yh.admin.mapper;

import com.yixuan.yh.admin.entity.Audit;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditMapper {
    @Insert("insert into audit (admin_id, request_method, request_path, request_body) values(#{adminId}, #{requestMethod}, #{requestPath}, #{requestBody})")
    void insert(Audit audit);

    @Delete("delete from audit where id = #{id}")
    void delete(Long id);

    List<Audit> selectByCond(Long id, Long adminId, String requestMethod, String requestPath, String createdTime);

    @Select("select * from audit where id > #{lastMaxId} limit 18")
    List<Audit> selectPage(Long lastMaxId);
}
