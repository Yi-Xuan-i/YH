package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.multi.UserFriendWithBasicInfo;
import com.yixuan.yh.user.pojo.response.UserFriendResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FollowMapStruct {
    FollowMapStruct INSTANCE = Mappers.getMapper(FollowMapStruct.class);

    UserFriendResponse toUserFriendResponse(UserFriendWithBasicInfo userFriendWithBasicInfo);
}
