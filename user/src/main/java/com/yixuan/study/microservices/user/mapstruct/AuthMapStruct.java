package com.yixuan.study.microservices.user.mapstruct;

import com.yixuan.study.microservices.user.entity.User;
import com.yixuan.study.microservices.user.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthMapStruct {
    AuthMapStruct INSTANCE = Mappers.getMapper(AuthMapStruct.class);

    User registerRequestToUser(RegisterRequest registerRequest);
}
