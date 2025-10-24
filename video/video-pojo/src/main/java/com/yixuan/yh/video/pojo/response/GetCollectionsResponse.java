package com.yixuan.yh.video.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCollectionsResponse {
    Long id;
    String name;
    Integer itemCount;
}
