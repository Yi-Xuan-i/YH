package com.yixuan.yh.video.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCollectionsItemRequest {
    private Long targetCollectionsId;
    private List<Long> ids;
}
