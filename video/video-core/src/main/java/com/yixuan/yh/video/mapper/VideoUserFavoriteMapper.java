package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserFavorite;
import com.yixuan.yh.video.pojo.entity.VideoUserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserFavoriteMapper {

    @Select("select count(*) from video_user_favorite where user_id = #{userId} and video_id = #{videoId} and status = 'FRONT'")
    boolean isFavorite(Long userId, Long videoId);

    void insert(VideoUserFavorite videoUserFavorite);


    void insertBatch(List<VideoUserFavorite> videoUserFavoriteList);
}
