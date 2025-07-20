package com.yixuan.yh.chat.mapstruct;

import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.entity.multi.RecentContact;
import com.yixuan.yh.chat.response.ConversationMessageResponse;
import com.yixuan.yh.chat.response.RecentContactResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConversationMapStruct {
    ConversationMapStruct INSTANCE = Mappers.getMapper(ConversationMapStruct.class);

    RecentContactResponse toRecentContactResponse(RecentContact recentContact);
    ConversationMessageResponse toConversationMessageResponse(ChatMessage chatMessage);
}
