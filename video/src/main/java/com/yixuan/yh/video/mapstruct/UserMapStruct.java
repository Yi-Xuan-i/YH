package com.yixuan.yh.video.mapstruct;

import com.yixuan.yh.video.entity.User;
import com.yixuan.yh.video.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapStruct {
    UserMapStruct INSTANCE = Mappers.getMapper(UserMapStruct.class);

    User registerRequestToUser(RegisterRequest registerRequest);
}
