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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemoryService {

    private static final int SEARCH_TOP_K = 12;
    private static final double SEARCH_THRESHOLD = 0.35;
    private static final int KEYWORD_FALLBACK_TOP_K = 48;
    private static final int DUPLICATE_CHECK_TOP_K = 3;
    private static final double DUPLICATE_THRESHOLD = 0.88;
    private static final int MAX_MEMORY_LENGTH = 500;
    private static final Set<String> QUERY_STOP_WORDS = Set.of(
            "用户", "我的", "他的", "她的", "它的", "这个", "那个", "一下", "什么", "多少", "是谁", "是啥", "信息", "相关"
    );

    private final VectorStore vectorStore;

    @McpTool(name = "archival_memory_insert", description = "当用户在对话中提到了以前不知道的新信息（如新的喜好、职业变化、重要关系人、宠物、计划等）时，调用此工具。不要记录短期的日常寒暄或情绪。")
    public Mono<Boolean> archivalMemoryInsert(@McpToolParam(description = "需要存入的记忆") String memory, McpMeta meta) {
        return Mono.fromCallable(() -> {
            String userId = getUserId(meta);
            String normalizedMemory = normalizeMemory(memory);
            if (userId.isBlank() || normalizedMemory.isBlank()) {
                return false;
            }
            if (isDuplicateMemory(userId, normalizedMemory)) {
                System.out.println("跳过重复记忆: " + normalizedMemory);
                return false;
            }

            Document document = new Document(normalizedMemory, Map.of(
                    "user_id", userId,
                    "created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "memory_kind", "archival"
            ));
            vectorStore.add(List.of(document));
            System.out.println("已存储记忆: " + normalizedMemory);
            return true;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name = "archival_memory_search", description = "当用户的提问涉及过去发生的事、特定人物、过往偏好、或指代不明的历史上下文时，调用此工具去检索用户的长期记忆。")
    public Mono<String> archivalMemorySearch(@McpToolParam(description = "搜索词") String query, McpMeta meta) {
        String userId = getUserId(meta);
        System.out.println("正在搜索记忆，查询词: " + query + ", 用户ID: " + userId);
        return Mono.fromCallable(() -> {
            String normalizedQuery = normalizeQuery(query);
            if (userId.isBlank() || normalizedQuery.isBlank()) {
                return "未找到相关的历史记忆。";
            }

            List<Document> docs = searchUserMemories(normalizedQuery, userId, SEARCH_TOP_K, SEARCH_THRESHOLD);
            if (docs.isEmpty()) {
                System.out.println("初始搜索未找到相关记忆，尝试扩展查询词进行二次搜索。");
                docs = searchUserMemories(expandQuery(normalizedQuery), userId, SEARCH_TOP_K, SEARCH_THRESHOLD);
            }
            if (docs.isEmpty()) {
                System.out.println("向量检索未找到相关记忆，尝试关键词兜底检索。");
                docs = searchUserMemoriesByKeywordFallback(normalizedQuery, userId, SEARCH_TOP_K);
            }

            if (docs.isEmpty()) {
                return "未找到相关的历史记忆。";
            }
            for (Document doc : docs) {
                System.out.println("找到相关记忆: " + doc.getText());
            }

            return docs.stream()
                    .filter(Objects::nonNull)
                    .map(this::formatMemory)
                    .collect(Collectors.joining("\n---\n", "找到以下相关历史记忆，请只采用与用户当前问题有关的内容：\n", ""));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private boolean isDuplicateMemory(String userId, String memory) {
        return !searchUserMemories(memory, userId, DUPLICATE_CHECK_TOP_K, DUPLICATE_THRESHOLD).isEmpty();
    }

    private List<Document> searchUserMemories(String query, String userId, int topK, double threshold) {
        List<Document> docs = searchByVectorStoreFilter(query, userId, topK, threshold);
        if (!docs.isEmpty()) {
            return docs;
        }
        return searchAndFilterInMemory(query, userId, topK, threshold);
    }

    private List<Document> searchUserMemoriesByKeywordFallback(String query, String userId, int topK) {
        List<String> queries = buildFallbackQueries(query);
        Map<String, Document> candidates = new LinkedHashMap<>();
        for (String fallbackQuery : queries) {
            searchByVectorStoreFilter(fallbackQuery, userId, KEYWORD_FALLBACK_TOP_K).forEach(doc -> putCandidate(candidates, doc));
            searchAndFilterInMemory(fallbackQuery, userId, KEYWORD_FALLBACK_TOP_K).forEach(doc -> putCandidate(candidates, doc));
        }
        return candidates.values().stream()
                .map(doc -> Map.entry(doc, keywordScore(query, doc.getText())))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<Document, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Document> searchByVectorStoreFilter(String query, String userId, int topK, double threshold) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression(buildUserFilter(userId))
                .similarityThreshold(threshold)
                .build();
        return vectorStore.similaritySearch(request);
    }

    private List<Document> searchByVectorStoreFilter(String query, String userId, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression(buildUserFilter(userId))
                .build();
        return vectorStore.similaritySearch(request);
    }

    private List<Document> searchAndFilterInMemory(String query, String userId, int topK, double threshold) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(Math.max(topK * 3, topK))
                .similarityThreshold(threshold)
                .build();
        return vectorStore.similaritySearch(request).stream()
                .filter(doc -> isLegacyOrUserMemory(doc, userId))
                .limit(topK)
                .toList();
    }

    private List<Document> searchAndFilterInMemory(String query, String userId, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(Math.max(topK * 3, topK))
                .build();
        return vectorStore.similaritySearch(request).stream()
                .filter(doc -> isLegacyOrUserMemory(doc, userId))
                .limit(topK)
                .toList();
    }

    private boolean isLegacyOrUserMemory(Document doc, String userId) {
        Map<String, Object> metadata = doc.getMetadata();
        Object metadataUserId = metadata.get("user_id");
        if (metadataUserId != null) {
            return userId.equals(String.valueOf(metadataUserId));
        }
        return !metadata.containsKey("source");
    }

    private String formatMemory(Document doc) {
        Object createdAt = doc.getMetadata().get("created_at");
        if (createdAt == null) {
            return doc.getText();
        }
        return "记录时间: " + createdAt + "\n记忆: " + doc.getText();
    }

    private String normalizeMemory(String memory) {
        if (memory == null) {
            return "";
        }
        String normalized = memory.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= MAX_MEMORY_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_MEMORY_LENGTH);
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            return "";
        }
        return query.replaceAll("\\s+", " ").trim();
    }

    private String expandQuery(String query) {
        return "用户当前问题: " + query + "\n检索目标: 用户长期记忆中的人物、姓名、偏好、经历、计划、项目、关系和历史上下文。";
    }

    private List<String> buildFallbackQueries(String query) {
        List<String> queries = new ArrayList<>();
        queries.add(query);
        if (containsAny(query, List.of("姓名", "名字", "昵称", "称呼", "叫什么", "叫啥", "贵姓"))) {
            queries.add("用户的名字 姓名 昵称 称呼 用户叫什么 用户叫");
        }
        if (containsAny(query, List.of("喜欢", "偏好", "爱好", "口味"))) {
            queries.add("用户喜欢 偏好 爱好 口味");
        }
        if (containsAny(query, List.of("职业", "工作", "岗位", "公司"))) {
            queries.add("用户职业 工作 岗位 公司");
        }
        return queries;
    }

    private int keywordScore(String query, String memory) {
        if (memory == null || memory.isBlank()) {
            return 0;
        }
        int score = 0;
        String normalizedQuery = query.replaceAll("\\s+", "");
        String normalizedMemory = memory.replaceAll("\\s+", "");
        for (String keyword : extractQueryKeywords(normalizedQuery)) {
            if (normalizedMemory.contains(keyword)) {
                score += keyword.length() >= 2 ? 2 : 1;
            }
        }
        if (containsAny(normalizedQuery, List.of("姓名", "名字", "昵称", "称呼", "叫什么", "叫啥", "贵姓"))
                && containsAny(normalizedMemory, List.of("姓名", "名字", "昵称", "称呼", "叫"))) {
            score += 6;
        }
        return score;
    }

    private List<String> extractQueryKeywords(String query) {
        return List.of(query.split("[,，。；;、\\s]+")).stream()
                .map(String::trim)
                .filter(keyword -> keyword.length() >= 2)
                .filter(keyword -> !QUERY_STOP_WORDS.contains(keyword))
                .toList();
    }

    private boolean containsAny(String text, List<String> keywords) {
        return keywords.stream().anyMatch(text::contains);
    }

    private void putCandidate(Map<String, Document> candidates, Document doc) {
        if (doc != null && doc.getText() != null) {
            candidates.putIfAbsent(doc.getText() + doc.getMetadata(), doc);
        }
    }

    private String getUserId(McpMeta meta) {
        if (meta == null || meta.get("userId") == null) {
            return "";
        }
        return String.valueOf(meta.get("userId")).trim();
    }

    private String buildUserFilter(String userId) {
        return "user_id == '" + userId.replace("'", "\\'") + "'";
    }
}
