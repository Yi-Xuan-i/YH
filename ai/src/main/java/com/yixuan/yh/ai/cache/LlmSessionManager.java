package com.yixuan.yh.ai.cache;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LlmSessionManager {

    private final Map<Long, Sinks.Many<String>> sessionSinks = new ConcurrentHashMap<>();

    /**
     * 获取或创建属于该会话的 Sink
     * 使用 replay().all() 或 replay().limit(1000) 确保新加入的连接能拿到历史数据
     */
    public Sinks.Many<String> getOrCreateSink(Long sessionId) {
        return sessionSinks.computeIfAbsent(sessionId, id -> 
                Sinks.many().replay().all()
        );
    }

    /**
     * 获取供前端消费的 Flux
     */
    public Flux<String> getStream(Long sessionId) {
        Sinks.Many<String> sink = sessionSinks.get(sessionId);
        if (sink == null) {
            return Flux.empty();
        }
        return sink.asFlux();
    }

    public boolean hasSession(Long sessionId) {
        return sessionSinks.containsKey(sessionId);
    }

    /**
     * 当大模型彻底生成完毕后，清理内存
     */
    public void removeSession(Long sessionId) {
        sessionSinks.remove(sessionId);
    }
}
