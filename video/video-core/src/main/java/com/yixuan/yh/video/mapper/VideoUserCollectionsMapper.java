package com.yixuan.yh.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserCollections;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCollectionsMapper extends BaseMapper<VideoUserCollections> {

    List<VideoUserCollections> selectByUserId(Long userId, Long lastMinId);

    @Select("select user_id from video_user_collections where id = #{collectionsId}")
    Long selectUserIdById(Long collectionsId);
}
