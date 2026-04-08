package com.yixuan.yh.ai.controller._public;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/test")
public class TestController {

    @Autowired
    private VectorStore vectorStore;

    @GetMapping
    public String test() {
        Resource resource = new PathResource("D:\\test.txt");

        TextReader reader = new TextReader(resource);
        reader.getCustomMetadata().put("source", "knowledge.txt");

        List<Document> docs = reader.read();

        List<Document> chunks = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withKeepSeparator(true)
                .build()
                .apply(docs);

        vectorStore.add(chunks);

        return  "666";
    }
}
