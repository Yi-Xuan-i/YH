package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.request.ProfileRequest;
import com.yixuan.yh.user.pojo.response.ProfileResponse;
import com.yixuan.yh.user.pojo.response.UserSearchResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    @Select("select name from user where id = #{id}")
    String selectNameById(String id);

    List<User> selectUserInfoInList(List<Long> idList);

    @Select("select user.id, name, avatar_url, bio, CASE WHEN f.id IS NOT NULL THEN true ELSE false END AS is_followed from user left join user_follow f on follower_id = #{userId} and followee_id = user.id  where name like CONCAT(#{query}, '%')")
    List<UserSearchResponse> selectUserByNamePrefix(Long userId, String query);
}
