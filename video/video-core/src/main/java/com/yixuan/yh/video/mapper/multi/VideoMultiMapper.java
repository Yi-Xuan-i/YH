package com.yixuan.yh.video.mapper.multi;

import com.yixuan.yh.video.pojo.entity.multi.VideoWithInteractionStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMultiMapper {
    List<VideoWithInteractionStatus> selectMainRandom(@Param("userId") Long userId);

    VideoWithInteractionStatus selectMainOne(@Param("videoId") Long videoId, @Param("userId") Long userId);
}
