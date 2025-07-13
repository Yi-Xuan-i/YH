package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.UserPreferences;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPreferencesMapper {
    @Select("select video_pref_vector from user_preferences where user_id = #{userId}")
    List<byte[]> selectVideoPrefVectorByUserId(Long userId);

    @Insert("insert into user_preferences (id, user_id, video_pref_vector) values (#{id}, #{userId}, #{videoPrefVector})")
    void insert(UserPreferences userPreferences);
}
