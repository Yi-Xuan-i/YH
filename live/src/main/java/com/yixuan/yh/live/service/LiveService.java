package com.yixuan.yh.live.service;

import com.yixuan.yh.live.request.StartLiveRequest;
import com.yixuan.yh.live.response.GetLiveResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface LiveService {
    Long postStartLive(Long userId, StartLiveRequest startLiveRequest);

    void publishCallback(Long roomId, String clientId, String token) throws BadRequestException;

    void unpublishCallback(Long roomId, String clientId) throws BadRequestException;

    List<GetLiveResponse> getLivesByQuery(String query);
}
