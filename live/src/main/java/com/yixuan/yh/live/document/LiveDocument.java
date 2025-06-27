package com.yixuan.yh.live.document;

import lombok.Data;
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

    @Field(type = FieldType.Byte)
    private Byte status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss", index = false)
    private LocalDateTime createdTime;

    public enum LiveStatus {
        LIVING((byte) 0),
        ENDED((byte) 1);

        private final byte code;

        LiveStatus(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }
    }
}