package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.request.VideoLikeBatchRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {
    @Insert("insert into video (id, creator_id, url, cover_url, description) value (#{id}, #{creatorId}, #{url}, #{coverUrl}, #{description})")
    void insert(Video video);

    @Select("select url from video where id = #{id}")
    String selectVideoUrlById(Long id);

    void updateBatch(List<VideoLikeBatchRequest.LikeIncr> likeIncrList);
}
