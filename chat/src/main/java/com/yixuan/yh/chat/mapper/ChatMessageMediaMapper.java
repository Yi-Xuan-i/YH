package com.yixuan.yh.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.chat.entity.ChatMessageMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChatMessageMediaMapper extends BaseMapper<ChatMessageMedia> {

    @Select("select id, message_id, media_type, url, cover_url, created_at from chat_message_media where id = #{mediaId} and message_id is null")
    ChatMessageMedia selectUnboundById(Long mediaId);

    @Update("update chat_message_media set message_id = #{messageId} where id = #{mediaId} and message_id is null")
    int bindMessage(Long mediaId, Long messageId);
}
