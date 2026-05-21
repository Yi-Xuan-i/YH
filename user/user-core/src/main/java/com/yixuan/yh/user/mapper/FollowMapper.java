package com.yixuan.yh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.user.pojo.entity.UserFollow;
import com.yixuan.yh.user.pojo.response.FollowUserResponse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    @Select("""
            select uf.id,
                   u.id as userId,
                   u.name,
                   u.avatar_url as avatarUrl,
                   case when reverse_uf.id is not null then 1 else 0 end as followStatus
            from user_follow uf
            inner join user u on u.id = uf.followee_id
            left join user_follow reverse_uf
                   on reverse_uf.follower_id = uf.followee_id
                  and reverse_uf.followee_id = #{userId}
            where uf.follower_id = #{userId}
              and (#{lastId} is null or uf.id < #{lastId})
            order by uf.id desc
            limit 10
            """)
    List<FollowUserResponse> selectFollowingPage(Long userId, Long lastId);

    @Select("""
            select uf.id,
                   u.id as userId,
                   u.name,
                   u.avatar_url as avatarUrl,
                   case when forward_uf.id is not null then 1 else 0 end as followStatus
            from user_follow uf
            inner join user u on u.id = uf.follower_id
            left join user_follow forward_uf
                   on forward_uf.follower_id = #{userId}
                  and forward_uf.followee_id = uf.follower_id
            where uf.followee_id = #{userId}
              and (#{lastId} is null or uf.id < #{lastId})
            order by uf.id desc
            limit 10
            """)
    List<FollowUserResponse> selectFollowerPage(Long userId, Long lastId);
}
