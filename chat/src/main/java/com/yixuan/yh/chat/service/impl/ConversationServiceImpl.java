package com.yixuan.yh.chat.service.impl;

import com.yixuan.yh.chat.entity.ChatConversation;
import com.yixuan.yh.chat.entity.ChatMessage;
import com.yixuan.yh.chat.entity.multi.RecentContact;
import com.yixuan.yh.chat.mapper.ChatConversationMapper;
import com.yixuan.yh.chat.mapper.ChatMessageMapper;
import com.yixuan.yh.chat.mapstruct.ConversationMapStruct;
import com.yixuan.yh.chat.response.ConversationMessageResponse;
import com.yixuan.yh.chat.response.RecentContactResponse;
import com.yixuan.yh.chat.service.ConversationService;
import com.yixuan.yh.user.feign.UserPrivateClient;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ChatConversationMapper chatConversationMapper;
    @Autowired
    private UserPrivateClient userPrivateClient;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public List<RecentContactResponse> getRecentContacts(Long userId) {
        List<RecentContact> recentContactList = chatConversationMapper.selectRecentContacts(userId);

        // 转换实体类
        List<RecentContactResponse> recentContactResponseList = new ArrayList<>(recentContactList.size());
        for (RecentContact recentContact : recentContactList) {
            recentContactResponseList.add(ConversationMapStruct.INSTANCE.toRecentContactResponse(recentContact));
        }

        /* 完善联系人数据 */
        // 提取出contactId
        List<Long> contactIdList = recentContactResponseList.stream().map(RecentContactResponse::getContactId).toList();
        // 记录原数组索引
        List<Integer> sortedIndices = IntStream.range(0, contactIdList.size())
                .boxed()
                .sorted(Comparator.comparingLong(contactIdList::get))
                .toList();
        // 获取排序后的数组
        List<Long> sortedContactIdList = sortedIndices.stream().map(contactIdList::get).toList();

        List<UserInfoInListResponse> responseList = userPrivateClient.getUserInfoInList(sortedContactIdList).getData();
        for (int i = 0; i < responseList.size(); i++) {
            RecentContactResponse recentContactResponse = recentContactResponseList.get(sortedIndices.get(i));
            UserInfoInListResponse userInfoInListResponse = responseList.get(i);
            recentContactResponse.setContactName(userInfoInListResponse.getName());
            recentContactResponse.setContactAvatar(userInfoInListResponse.getAvatarUrl());
        }

        return recentContactResponseList;
    }

    @Override
    public List<ConversationMessageResponse> getConversationMessages(Long userId, Long conversationId) throws BadRequestException {
        // 鉴权
        ChatConversation conversation = chatConversationMapper.selectBothIdById(conversationId);
        if (!(userId.equals(conversation.getUser1Id()) || userId.equals(conversation.getUser2Id()))) {
            throw new BadRequestException("你没有权限！");
        }

        List<ChatMessage> chatMessageList = chatMessageMapper.selectByConversationId(conversationId);

        List<ConversationMessageResponse> conversationMessageResponseList = new ArrayList<>(chatMessageList.size());
        for (ChatMessage chatMessage : chatMessageList) {
            ConversationMessageResponse conversationMessage = ConversationMapStruct.INSTANCE.toConversationMessageResponse(chatMessage);
            conversationMessage.setIsUser(userId.equals(chatMessage.getSenderId()));

            conversationMessageResponseList.add(conversationMessage);
        }

        return conversationMessageResponseList;
    }
}
