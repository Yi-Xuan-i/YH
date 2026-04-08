package com.yixuan.yh.mcp.server;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final VectorStore vectorStore;

    @Tool(description = "根据用户问题搜索与商城相关的内容")
    public String searchKnowledge(String keyword) {

        SearchRequest request = SearchRequest.builder()
                .query(keyword)
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
    }
}