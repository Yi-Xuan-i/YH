package com.yixuan.yh.mcp.server;

import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpMeta;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemoryService {

    private final VectorStore vectorStore;

    @McpTool(name = "archival_memory_insert", description = "当用户在对话中提到了以前不知道的新信息（如新的喜好、职业变化、重要关系人、宠物、计划等）时，调用此工具。不要记录短期的日常寒暄或情绪。")
    public Mono<Boolean> archivalMemoryInsert(@McpToolParam(description = "需要存入的记忆") String memory, McpMeta meta) {
        System.out.println(meta.get("userId"));
        return Mono.fromCallable(() -> {
            Document document = new Document(memory);
            vectorStore.add(List.of(document));
            return true;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name = "archival_memory_search", description = "当用户的提问涉及过去发生的事、特定人物、过往偏好、或指代不明的历史上下文时，调用此工具去检索用户的长期记忆。")
    public Mono<String> archivalMemorySearch(@McpToolParam(description = "搜索词") String query, McpMeta meta) {
        System.out.println(meta.get("userId"));
        return Mono.fromCallable(() -> {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(3)
                    .similarityThreshold(0.65)
                    .build();

            List<Document> docs = vectorStore.similaritySearch(request);

            if (docs.isEmpty()) {
                return "未找到相关的历史记忆。";
            }

            return docs.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n---\n", "找到以下相关历史记录：\n", ""));
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
