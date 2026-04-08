package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUserComment;
import com.yixuan.yh.video.pojo.entity.multi.CommentWithReceiver;
import com.yixuan.yh.video.pojo.mq.VideoCommentIncrMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoUserCommentMapper {

    @Insert("insert into video_user_comment (id, video_id, content, user_id, root_id, parent_id) values(#{id}, #{videoId}, #{content}, #{userId}, #{rootId}, #{parentId})")
    void insert(VideoUserComment videoUserComment);

    @Select("select root_id from video_user_comment where id = #{id}")
    Long selectRootIdById(Long id);

    void updateReplyBatch(List<VideoCommentIncrMessage.ReplyIncr> replyIncrList);

    List<VideoUserComment> selectDirectComment(Long videoId, Long lastMinId);

    List<CommentWithReceiver> selectReplyComment(Long commentId, Long lastMaxId);
}
