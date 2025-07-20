package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.UserNewFriendMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewFriendMessageMapper {

    @Insert("insert into user_new_friend_message (id, user_id, friend_id) values (#{id}, #{userId}, #{friendId})")
    void insert(UserNewFriendMessage userNewFriendMessage);
}
