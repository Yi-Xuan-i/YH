package com.yixuan.yh.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("intent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Intent {
    Long id;
    String intent;
    String response;
}
