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
public class CustomerService {

    private final VectorStore vectorStore;

    @McpTool(name = "搜索商城相关内容", description = "可以根据用户问题搜索与商城相关的内容")
    public Mono<String> searchKnowledge(@McpToolParam(description = "用户问题") String query) {

        return Mono.fromCallable(() -> {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(3)
                    .build();

            List<Document> docs = vectorStore.similaritySearch(request);

            return docs.stream()
                    .map(doc -> {
                        String source = (String) doc.getMetadata()
                                .getOrDefault("source", "未知来源");

                        return String.format(
                                "【来源：%s】\n内容：%s",
                                source,
                                doc.getText()
                        );
                    })
                    .collect(Collectors.joining("\n\n---\n\n"));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}