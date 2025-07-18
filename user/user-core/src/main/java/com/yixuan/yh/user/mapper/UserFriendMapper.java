package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.UserFriend;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFriendMapper {
    @Insert("insert ignore into user_friend (id, user_id, friend_id, created_time) values (#{id}, #{userId}, #{friendId}, #{createdTime}), (#{id}, #{friendId}, #{userId}, #{createdTime})")
    boolean insertEach(UserFriend userFriend);

    @Delete("delete from user_friend where (user_id = #{userId} and friend_id = #{followeeId}) or (user_id = #{followeeId} and friend_id = #{userId})")
    boolean deleteByFollowerIdAndFolloweeId(Long userId, Long followeeId);
}
