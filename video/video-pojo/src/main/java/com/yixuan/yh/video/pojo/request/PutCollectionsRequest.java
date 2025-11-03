package com.yixuan.yh.video.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutCollectionsRequest {
    private String name;
    private Integer isPublic;
}
