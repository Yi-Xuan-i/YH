package com.yixuan.yh.video.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InteractionMapStruct {

    InteractionMapStruct INSTANCE = Mappers.getMapper(InteractionMapStruct.class);
}
