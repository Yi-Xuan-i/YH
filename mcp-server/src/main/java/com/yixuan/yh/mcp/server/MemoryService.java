package com.yixuan.yh.mcp.server;

import lombok.RequiredArgsConstructor;
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

    @McpTool(name = "archival_memory_insert", description = "将重要的事实、知识或用户长期偏好存入归档记忆库（硬盘）")
    public Mono<Boolean> archivalMemoryInsert(@McpToolParam(description = "需要存入的记忆") String memory) {
        return Mono.fromCallable(() -> {
            Document document = new Document(memory);
            vectorStore.add(List.of(document));

            return true;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name = "archival_memory_search", description = "当主内存中找不到相关信息时，搜索归档记忆库（硬盘）中的历史事实")
    public Mono<String> archivalMemorySearch(@McpToolParam(description = "搜索词") String query) {
        return Mono.fromCallable(() -> {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(3)
                    .similarityThreshold(0.85)
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
