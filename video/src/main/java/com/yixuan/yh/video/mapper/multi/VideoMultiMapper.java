package com.yixuan.yh.video.mapper.multi;

import com.yixuan.yh.video.response.VideoMainResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMultiMapper {
    @Select("SELECT video.id, creator_id, name as creatorName, avatar_url as creatorAvatar, url, description, likes, comments, favorites \n" +
            "FROM video \n" +
            "JOIN user on video.creator_id = user.id " +
            "WHERE video.id >= (\n" +
            "    SELECT MIN(id) + FLOOR(RAND() * (MAX(id) - MIN(id))) \n" +
            "    FROM video\n" +
            ") \n" +
            "LIMIT 5;")
    List<VideoMainResponse> selectMainRandom();
}
