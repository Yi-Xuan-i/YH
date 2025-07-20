package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.UserFollow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Insert("insert ignore user_follow (id, follower_id, followee_id, created_time) values(#{id}, #{followerId}, #{followeeId}, #{createdTime})")
    boolean insertIgnore(UserFollow userFollow);

    @Select("select count(*) from user_follow where follower_id = #{followerId} and followee_id = #{followerId}")
    boolean selectIsRelationExist(Long followerId, Long followeeId);

    @Delete("delete from user_follow where follower_id = #{followerId} and followee_id = #{followeeId}")
    boolean deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    List<Boolean> selectFollowStatusBatch(Long followerId, List<Long> followeeIdList);
}
