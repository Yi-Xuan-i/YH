package com.yixuan.yh.chat.service;

import com.yixuan.yh.chat.response.ConversationMessageResponse;
import com.yixuan.yh.chat.response.RecentContactResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface ConversationService {
    List<RecentContactResponse> getRecentContacts(Long userId);

    List<ConversationMessageResponse> getConversationMessages(Long userId, Long conversationId, Long lastMinId) throws BadRequestException;
}
