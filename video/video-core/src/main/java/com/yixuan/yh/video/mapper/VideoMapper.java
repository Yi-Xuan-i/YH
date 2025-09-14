package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {
    @Select("select * from video limit 1")
    Video selectFirst();

    @Insert("insert into video (id, creator_id, url, cover_url, description) value (#{id}, #{creatorId}, #{url}, #{coverUrl}, #{description})")
    void insert(Video video);

    @Select("select url from video where id = #{id}")
    String selectVideoUrlById(Long id);

    void updateLikeBatch(List<VideoInteractionBatchRequest.Incr> likeIncrList);

    void updateFavoriteBatch(List<VideoInteractionBatchRequest.Incr> favoriteIncrList);

    @Select("select status from video where id = #{id}")
    Video.VideoStatus selectVideoStatusUrlById(Long id);

    @Select("select id, url, cover_url, description, likes, comments, favorites, created_time from video where creator_id = #{userId} and status = 'UPLOADED' order by created_time desc")
    List<Video> selectUploadedVideoByUserId(Long userId);

    @Select("select id, url, cover_url, description, likes, comments, favorites, created_time from video where creator_id = #{userId} and status = 'PUBLISHED' order by created_time desc")
    List<Video> selectPublishedVideoByUserId(Long userId);

    @Select("select id, url, cover_url, description, created_time from video where creator_id = #{userId} and status = 'PROCESSING' order by created_time desc")
    List<Video> selectProcessingVideoByUserId(Long userId);

    @Select("select id, url, cover_url, description, created_time from video where creator_id = #{userId} and status = 'REJECTED' order by created_time desc")
    List<Video> selectRejectedVideoByUserId(Long userId);

    @Select("select count(*) from video where id = #{videoId}")
    boolean selectIsExistById(Long videoId);
}
