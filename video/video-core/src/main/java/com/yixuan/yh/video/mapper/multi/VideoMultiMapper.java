package com.yixuan.yh.video.mapper.multi;

import com.yixuan.yh.video.pojo.response.VideoMainResponse;
import com.yixuan.yh.video.pojo.response.VideoMainWithInteractionResponse;
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

    @Select("SELECT \n" +
            "  video.id, \n" +
            "  creator_id, \n" +
            "  name AS creatorName, \n" +
            "  avatar_url AS creatorAvatar, \n" +
            "  url, \n" +
            "  description, \n" +
            "  likes, \n" +
            "  comments, \n" +
            "  favorites,\n" +
            "  IF(video_user_like.user_id IS NOT NULL, 1, 0) AS is_like,\n" +
            "  IF(video_user_favorite.user_id IS NOT NULL, 1, 0) AS is_favorite\n" +
            "FROM video \n" +
            "JOIN user ON video.creator_id = user.id\n" +
            "LEFT JOIN video_user_like ON (\n" +
            "  video.id = video_user_like.video_id \n" +
            "  AND video_user_like.user_id = #{userId}\n" +
            "  AND video_user_like.status = 'FRONT' " +
            ")\n" +
            "LEFT JOIN video_user_favorite ON (\n" +
            "  video.id = video_user_favorite.video_id \n" +
            "  AND video_user_favorite.user_id = #{userId}\n" +
            "  AND video_user_favorite.status = 'FRONT' " +
            ")\n" +
            "WHERE video.id >= (\n" +
            "  SELECT MIN(id) + FLOOR(RAND() * (MAX(id) - MIN(id))) \n" +
            "  FROM video\n" +
            ") \n" +
            "LIMIT 5;")
    List<VideoMainWithInteractionResponse> selectMainWithInteractionStatusRandom(Long userId);
}
