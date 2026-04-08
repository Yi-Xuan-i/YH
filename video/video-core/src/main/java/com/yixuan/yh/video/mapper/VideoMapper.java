package com.yixuan.yh.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithFavorite;
import com.yixuan.yh.video.pojo.entity.multi.VideoWithLike;
import com.yixuan.yh.video.pojo.mq.VideoCommentIncrMessage;
import com.yixuan.yh.video.pojo.request.VideoInteractionBatchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    @Select("select * from video limit 1")
    Video selectFirst();

    @Select("select url from video where id = #{id}")
    String selectVideoUrlById(Long id);

    void updateLikeBatch(List<VideoInteractionBatchRequest.Incr> likeIncrList);

    void updateFavoriteBatch(List<VideoInteractionBatchRequest.Incr> favoriteIncrList);

    void updateCommentBatch(List<VideoCommentIncrMessage.CommentIncr> commentIncrList);

    @Select("select status from video where id = #{id}")
    Video.VideoStatus selectVideoStatusUrlById(Long id);

    @Select("select id, url, cover_url, description, likes, comments, favorites, created_time from video where creator_id = #{userId} and status = 'UPLOADED' order by created_time desc")
    List<Video> selectUploadedVideoByUserId(Long userId);

    List<Video> selectPublishedVideoByUserId(Long userId, Long lastMinId);

    List<Video> selectProcessingVideoByUserId(Long userId, Long lastMinId);

    List<Video> selectRejectedVideoByUserId(Long userId, Long lastMinId);

    @Select("select count(*) from video where id = #{videoId}")
    boolean selectIsExistById(Long videoId);

    List<VideoWithLike> selectLikeVideoByUserId(Long userId, Long lastMinId);

    List<VideoWithFavorite> selectFavoriteVideoByUserId(Long userId, Long lastMinId);
}
