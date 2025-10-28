package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCollectionsMapper {

    List<VideoUserCollections> selectByUserId(Long userId, Long lastMinId);

    @Insert("insert into video_user_collections (id, user_id, name, update_at, created_at) values(#{id}, #{userId}, #{name}, #{updateAt}, #{createdAt})")
    void insert(VideoUserCollections videoUserCollections);
}
