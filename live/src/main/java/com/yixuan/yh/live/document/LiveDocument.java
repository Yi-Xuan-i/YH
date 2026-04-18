package com.yixuan.yh.live.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "live")
@Data
public class LiveDocument {
    @Id
    private Long roomId;

    @Field(type = FieldType.Long)
    private Long anchorId;

    @Field(type = FieldType.Keyword, index = false)
    private String anchorName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;


    @Field(type = FieldType.Keyword, index = false)
    private String coverUrl;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss", index = false)
    private LocalDateTime createdTime;

    @Getter
    @AllArgsConstructor
    public enum LiveStatus {
        LIVING(0),
        ENDED(1);

        private final int code;
    }
}