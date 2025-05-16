package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {
    @Insert("insert into user (id, phone_number, encoded_password) values (#{id}, #{phoneNumber}, #{encodedPassword})")
    void insertToRegister(User user);

    @Select("select id, password from user where phone_number = #{phoneNumber}")
    User selectIdAndPassword(String phoneNumber);
}
