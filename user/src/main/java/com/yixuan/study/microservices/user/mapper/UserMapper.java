package com.yixuan.study.microservices.user.mapper;

import com.yixuan.study.microservices.user.entity.User;
import com.yixuan.study.microservices.user.request.ProfileRequest;
import com.yixuan.study.microservices.user.response.ProfileResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Insert("insert into user (id, phone_number, name, encoded_password) values (#{id}, #{phoneNumber}, #{name}, #{encodedPassword})")
    void insertToRegister(User user);

    @Select("select id, encoded_password from user where phone_number = #{phoneNumber}")
    User selectIdAndPassword(String phoneNumber);

    @Select("select count(*) from user where phone_number = #{phoneNumber}")
    boolean selectIsPhoneNumberExist(String phoneNumber);

    @Select("select name, avatar_url, bio from user where id = #{userId}")
    ProfileResponse selectProfile(Long userId);

    @Select("select name, avatar_url from user where id = #{userId}")
    User selectBasicProfile(Long userId);

    @Update("update user set avatar_url = #{newAvatarUrl} where id = #{userId}")
    void updateAvatarUrl(Long userId, String newAvatarUrl);

    @Update("update user set name = #{profileRequest.name}, bio = #{profileRequest.bio} where id = #{userId}")
    void update(Long userId, ProfileRequest profileRequest);
}
