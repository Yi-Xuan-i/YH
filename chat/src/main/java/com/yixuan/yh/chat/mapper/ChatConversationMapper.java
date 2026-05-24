package com.yixuan.yh.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.chat.entity.ChatConversation;
import com.yixuan.yh.chat.entity.multi.RecentContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {

    @Update("update chat_conversation set user1_unread_count = user1_unread_count + 1, updated_time = now() where id = #{conversationId} and user1_id = #{userId}")
    void updateUser1UnreadCount(Long conversationId, Long userId);

    @Update("update chat_conversation set user2_unread_count = user2_unread_count + 1, updated_time = now() where id = #{conversationId} and user2_id = #{userId}")
    void updateUser2UnreadCount(Long conversationId, Long userId);

    @Select("select user1_id, user2_id from chat_conversation where id = #{conversationId}")
    ChatConversation selectBothIdById(Long conversationId);

    @Select("SELECT * FROM (\n" +
            "  (SELECT id as conversation_id, user2_id as contact_id, \n" +
            "          (SELECT content FROM chat_message WHERE conversation_id = chat_conversation.id ORDER BY id DESC LIMIT 1) as last_message, \n" +
            "          user1_unread_count as unread_count, updated_time \n" +
            "   FROM chat_conversation \n" +
            "   WHERE user1_id = #{userId} \n" +
//            "     AND updated_time < #{last_seen_time}\n" +
            "   ORDER BY updated_time DESC)\n" +
            "  UNION ALL\n" +
            "  \n" +
            "  (SELECT id as conversation_id, user1_id as contact_id, \n" +
            "          (SELECT content FROM chat_message WHERE conversation_id = chat_conversation.id ORDER BY id DESC LIMIT 1) as last_message, \n" +
            "          user2_unread_count as unread_count, updated_time  \n" +
            "   FROM chat_conversation \n" +
            "   WHERE user2_id = #{userId} \n" +
//            "     AND updated_time < #{last_seen_time}\n" +
            "   ORDER BY updated_time DESC)\n" +
            ") AS combined\n" +
            "ORDER BY updated_time DESC;\n")
    List<RecentContact> selectRecentContacts(Long userId);
}
