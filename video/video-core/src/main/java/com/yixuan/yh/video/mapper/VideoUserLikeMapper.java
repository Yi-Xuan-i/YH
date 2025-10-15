package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserLikeMapper {
    void insert(VideoUserLike videoUserLike);

    void insertBatch(List<VideoUserLike> videoUserLikeList);

    @Select("select count(*) from video_user_like where video_id = #{videoId} and user_id = #{userId} and status = 'FRONT'")
    boolean isLike(Long userId, Long videoId);
}
