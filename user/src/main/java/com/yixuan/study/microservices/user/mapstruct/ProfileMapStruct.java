package com.yixuan.study.microservices.user.mapstruct;

import com.yixuan.study.microservices.user.entity.User;
import com.yixuan.study.microservices.user.request.RegisterRequest;
import com.yixuan.study.microservices.user.response.BasicProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapStruct {
    ProfileMapStruct INSTANCE = Mappers.getMapper(ProfileMapStruct.class);

    BasicProfileResponse userToBasicProfileResponse(User user);
}
