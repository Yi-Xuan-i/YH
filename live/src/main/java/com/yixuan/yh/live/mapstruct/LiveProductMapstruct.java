package com.yixuan.yh.live.mapstruct;

import com.yixuan.yh.live.entity.LiveProduct;
import com.yixuan.yh.live.request.PostLiveProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LiveProductMapstruct {
    LiveProductMapstruct INSTANCE = Mappers.getMapper(LiveProductMapstruct.class);

    LiveProduct postLiveProductRequestToLiveProduct(PostLiveProductRequest postLiveProductRequest);
}
