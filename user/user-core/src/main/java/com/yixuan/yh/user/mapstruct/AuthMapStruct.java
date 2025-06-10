package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.User;
import com.yixuan.yh.user.pojo.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthMapStruct {
    AuthMapStruct INSTANCE = Mappers.getMapper(AuthMapStruct.class);

    User registerRequestToUser(RegisterRequest registerRequest);
}
