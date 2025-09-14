package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCommentMapper {

    @Insert("insert into video_user_comment (id, video_id, content, user_id, parent_id) values(#{id}, #{videoId}, #{content}, #{userId}, #{parentId})")
    void insert(VideoUserComment videoUserComment);

    @Select("select id, content, user_id, like_count, updated_at from video_user_comment where video_id = #{videoId} and parent_id is NULL order by updated_at desc")
    List<VideoUserComment> selectDirectComment(Long videoId);
}
