package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCollectionsMapper {
    @Select("select id, name, item_count from video_user_collections where user_id = #{userId}")
    List<VideoUserCollections> selectByUserId(Long userId);

    @Insert("insert into video_user_collections (id, user_id, name, update_at, created_at) values(#{id}, #{userId}, #{name}, #{updateAt}, #{createdAt})")
    void insert(VideoUserCollections videoUserCollections);
}
