package com.yixuan.yh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.user.pojo.entity.UserFollow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<UserFollow> {
    @Insert("insert ignore user_follow (id, follower_id, followee_id, created_time) values(#{id}, #{followerId}, #{followeeId}, #{createdTime})")
    boolean insertIgnore(UserFollow userFollow);

    @Select("select count(*) from user_follow where follower_id = #{followerId} and followee_id = #{followeeId}")
    boolean selectIsRelationExist(Long followerId, Long followeeId);

    @Delete("delete from user_follow where follower_id = #{followerId} and followee_id = #{followeeId}")
    boolean deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Select("select count(*) from user_follow where follower_id = #{userId}")
    Integer countFollowingByUserId(Long userId);

    @Select("select count(*) from user_follow where followee_id = #{userId}")
    Integer countFollowersByUserId(Long userId);

    List<Boolean> selectFollowStatusBatch(Long followerId, List<Long> followeeIdList);
}
