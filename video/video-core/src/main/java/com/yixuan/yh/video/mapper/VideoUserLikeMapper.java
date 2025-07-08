package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserLikeMapper {
    void insert(VideoUserLike videoUserLike);

    void insertBatch(List<VideoUserLike> videoUserLikeList);

    @Select("select count(*) from video_user_like where user_id = #{userId} and video_id = #{videoId} and status = 'LIKE'")
    boolean isLike(Long userId, Long videoId);
}
