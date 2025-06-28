package com.yixuan.yh.ai.websocket;

import com.yixuan.yh.ai.utils.ReactiveUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AiChatWebSocketHandler implements WebSocketHandler {

    private final ConcurrentHashMap<Long, List<String>> msgMap = new ConcurrentHashMap<>();
    @Autowired
    private ChatModel chatModel;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return ReactiveUserContext.getUserId().flatMapMany(id -> {
                    msgMap.put(id, new ArrayList<>());
                    return session.receive()
                            .flatMap(webSocketMessage -> {
                                        String msg = webSocketMessage.getPayloadAsText();

                                        // 构建上下文
                                        List<String> historyMessages = msgMap.get(id);
                                        StringBuilder contextBuilder = new StringBuilder();
                                        contextBuilder.append("system:").append("你的任务是负责与用户进行娱乐聊天，你的名字叫豆豆。注意聊天不要加表情，因为你输出的文本会转成语音与用户聊天！").append("\n");
                                        for (int i = 0; i < historyMessages.size(); i++) {
                                            String message = historyMessages.get(i);
                                            if (i % 2 == 0) {
                                                contextBuilder.append("user:");
                                            } else {
                                                contextBuilder.append("assistant:");
                                            }
                                            contextBuilder.append(": ")
                                                    .append(message)
                                                    .append("\n");
                                        }
                                        contextBuilder.append("user:").append(msg).append("\n");
                                        System.out.println(contextBuilder);

                                        return Mono.fromCallable(() -> {
                                                    Prompt prompt = new Prompt(contextBuilder.toString());
                                                    String text = chatModel.call(prompt).getResult().getOutput().getText();
                                                    msgMap.get(id).add(msg);
                                                    msgMap.get(id).add(text);
                                                    return text;
                                                })
                                                .subscribeOn(Schedulers.boundedElastic());
                                    }
                            );
                })
                .map(session::textMessage)
                .as(session::send);
    }

}