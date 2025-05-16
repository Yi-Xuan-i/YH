package com.yixuan.yh.admin.mapper;

import com.yixuan.yh.admin.entity.Admin;
import com.yixuan.yh.admin.request.PutAccountRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Insert("insert into admin (id, name, encoded_password) values(#{id}, #{name}, #{encodedPassword})")
    void insert(Admin admin);

    @Delete("delete from admin where id = #{id}")
    void delete(Long id);

    @Update("")
    void update(PutAccountRequest putAccountRequest);

    @Update("update admin set encoded_password = #{encodedPassword} where id = #{id}")
    void updatePassword(Long id, String encodedPassword);

    List<Admin> selectByCond(Long id, String name, String createdTime);

    @Select("select  id, name, created_time from admin")
    List<Admin> selectAll();

    @Select("select count(*) from admin")
    int selectCount();

    @Select("select count(*) from admin where name = #{name}")
    boolean selectIsNameExist(String name);

    @Select("select id, encoded_password from admin where name = #{name}")
    Admin selectIdAndPassword(String name);
}
