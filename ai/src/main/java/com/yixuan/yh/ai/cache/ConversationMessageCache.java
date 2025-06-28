package com.yixuan.yh.ai.cache;

import com.yixuan.yh.ai.entity.ConversationMessage;
import com.yixuan.yh.ai.repository.ConversationMessageRepository;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class ConversationMessageCache {

    @Autowired
    private ConversationMessageRepository conversationMessageRepository;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    public Flux<ConversationMessage> getMessage(Long conversationId) {
        return conversationMessageRepository.findByConversationIdOrderByMessageIdDesc(conversationId);
    }

    public Flux<ConversationMessage> addMessage(Long conversationId, List<String> msg) {
        ConversationMessage userMessage = new ConversationMessage();
        userMessage.setMessageId(snowflakeUtils.nextId());
        userMessage.setConversationId(conversationId);
        userMessage.setRole("user");
        userMessage.setContent(msg.get(0));

        ConversationMessage assistanceMessage = new ConversationMessage();
        assistanceMessage.setMessageId(snowflakeUtils.nextId());
        assistanceMessage.setConversationId(conversationId);
        assistanceMessage.setRole("assistant");
        assistanceMessage.setContent(msg.get(1));

        return conversationMessageRepository.saveAll(List.of(userMessage, assistanceMessage));
    }
}
