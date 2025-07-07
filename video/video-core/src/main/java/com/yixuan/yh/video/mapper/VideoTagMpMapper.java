package com.yixuan.yh.video.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoTagMpMapper {
    void insertBatch(Long video_id, List<Long> allTagList);
}
