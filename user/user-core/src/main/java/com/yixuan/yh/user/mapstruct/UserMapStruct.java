package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapStruct {
    UserMapStruct INSTANCE = Mappers.getMapper(UserMapStruct.class);

    UserInfoInListResponse toUserInfoInListResponse(User user);
}
