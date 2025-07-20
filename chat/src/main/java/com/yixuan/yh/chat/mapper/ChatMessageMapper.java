package com.yixuan.yh.chat.mapper;

import com.yixuan.yh.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("insert into chat_message (id, conversation_id, sender_id, content, content_type, created_time) values (#{id}, #{conversationId}, #{senderId}, #{content}, #{contentType}, #{createdTime})")
    void insert(ChatMessage chatMessage);

    @Select("select id, conversation_id, sender_id, content, content_type, created_time from chat_message where conversation_id = #{conversationId} order by created_time desc")
    List<ChatMessage> selectByConversationId(Long conversationId);
}
