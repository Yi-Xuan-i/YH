package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import com.yixuan.yh.video.pojo.entity.multi.CommentWithReceiver;
import com.yixuan.yh.videoprocessor.mq.VideoCommentIncrMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCommentMapper {

    @Insert("insert into video_user_comment (id, video_id, content, user_id, root_id, parent_id) values(#{id}, #{videoId}, #{content}, #{userId}, #{rootId}, #{parentId})")
    void insert(VideoUserComment videoUserComment);

    @Select("select id, content, user_id, reply_count, like_count, updated_at from video_user_comment where video_id = #{videoId} and parent_id is NULL order by updated_at desc")
    List<VideoUserComment> selectDirectComment(Long videoId);

    @Select("select root_id from video_user_comment where id = #{id}")
    Long selectRootIdById(Long id);

    void updateReplyBatch(List<VideoCommentIncrMessage.ReplyIncr> replyIncrList);

    @Select("select a.id, a.content, a.user_id as senderId, b.user_id as receiverId, a.reply_count, a.like_count, a.updated_at from video_user_comment a join video_user_comment b on a.parent_id = b.id  where a.root_id = #{commentId}")
    List<CommentWithReceiver> selectReplyComment(Long commentId);
}
