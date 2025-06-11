package com.yixuan.yh.live.mapstruct;

import com.yixuan.yh.live.document.LiveDocument;
import com.yixuan.yh.live.response.GetLiveResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LiveMapstruct {
    LiveMapstruct INSTANCE = Mappers.getMapper(LiveMapstruct.class);

    GetLiveResponse liveDocumentToGetLiveResponse(LiveDocument liveDocument);
}
