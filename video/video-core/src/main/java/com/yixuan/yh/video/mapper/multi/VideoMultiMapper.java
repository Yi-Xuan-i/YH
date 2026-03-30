package com.yixuan.yh.video.mapper.multi;

import com.yixuan.yh.video.pojo.entity.multi.VideoWithInteractionStatus;
import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMultiMapper {
    List<VideoWithInteractionStatus> selectMainRandom(Long userId);

    @Select("SELECT video.id, creator_id, name as creatorName, avatar_url as creatorAvatar, url, description, likes, comments, favorites \n" +
            "FROM video \n" +
            "JOIN user on video.creator_id = user.id " +
            "WHERE video.id = #{videoId}")
    VideoMainResponse selectMainOne(Long videoId);
}
