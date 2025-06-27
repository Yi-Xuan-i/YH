package com.yixuan.yh.live.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.live.entity.LiveProduct;
import com.yixuan.yh.live.mapper.LiveProductMapper;
import com.yixuan.yh.live.mapstruct.LiveProductMapstruct;
import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.GetLiveProductResponse;
import com.yixuan.yh.live.response.PostLiveProductResponse;
import com.yixuan.yh.live.service.LiveProductService;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class LiveProductServiceImpl implements LiveProductService {

    @Autowired
    private LiveProductMapper liveProductMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private MTClient mtClient;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public PostLiveProductResponse postLiveProduct(PostLiveProductRequest postLiveProductRequest) throws IOException {

        // 这里需要鉴权

        LiveProduct liveProduct = LiveProductMapstruct.INSTANCE.postLiveProductRequestToLiveProduct(postLiveProductRequest);
        liveProduct.setId(snowflakeUtils.nextId());
        liveProduct.setImageUrl(mtClient.upload(postLiveProductRequest.getImage()));

        liveProductMapper.insert(liveProduct);
        messagingTemplate.convertAndSend("/topic/room." + postLiveProductRequest.getRoomId(), new LiveMessage(LiveMessage.MessageType.PRODUCT, liveProduct.getId()));

        return new PostLiveProductResponse(liveProduct.getId(), liveProduct.getImageUrl());
    }

    @Override
    public List<GetLiveProductResponse> getRoomLiveProduct(Long roomId) {
        return liveProductMapper.selectByRoomId(roomId);
    }

    @Override
    public GetLiveProductResponse getLiveProduct(Long id) {
        return liveProductMapper.selectById(id);
    }
}
