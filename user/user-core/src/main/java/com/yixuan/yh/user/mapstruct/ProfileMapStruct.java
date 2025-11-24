package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.response.ProfileBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapStruct {
    ProfileMapStruct INSTANCE = Mappers.getMapper(ProfileMapStruct.class);

    ProfileBasicResponse userToBasicProfileResponse(User user);
}
