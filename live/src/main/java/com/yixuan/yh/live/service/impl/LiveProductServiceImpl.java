package com.yixuan.yh.live.service.impl;

import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.common.utils.AWSUtils;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.live.cache.LiveCache;
import com.yixuan.yh.live.entity.LiveProduct;
import com.yixuan.yh.live.mapper.LiveProductMapper;
import com.yixuan.yh.live.request.PostLiveProductRequest;
import com.yixuan.yh.live.response.LiveProductItemResponse;
import com.yixuan.yh.live.service.LiveProductService;
import com.yixuan.yh.live.websocket.pojo.LiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LiveProductServiceImpl implements LiveProductService {

    @Autowired
    private LiveProductMapper liveProductMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;
    @Autowired
    private AWSUtils awsUtils;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private LiveCache liveCache;

    @Override
    public void postMerchantProduct(Long userId, PostLiveProductRequest postLiveProductRequest) {
        if (!Objects.equals(liveCache.getAnchorId(postLiveProductRequest.getRoomId()), userId)) {
            throw new YHClientException("你没有权限！");
        }

        LiveProduct liveProduct = liveProductMapper.selectMerchantOnSaleProduct(userId, postLiveProductRequest.getProductId());
        if (liveProduct == null) {
            throw new YHClientException("商品不存在或未上架！");
        }

        liveProduct.setId(snowflakeUtils.nextId());
        liveProduct.setRoomId(postLiveProductRequest.getRoomId());

        liveProductMapper.insert(liveProduct);
        messagingTemplate.convertAndSend("/topic/room." + postLiveProductRequest.getRoomId(), new LiveMessage(LiveMessage.MessageType.PRODUCT, liveProduct.getProductId().toString()));
    }

    @Override
    public List<LiveProductItemResponse> getRoomLiveProduct(Long roomId) {
        List<LiveProductItemResponse> responseList = liveProductMapper.selectByRoomId(roomId);
        responseList.forEach(response -> response.setImageUrl(awsUtils.generateAccessUrl(response.getImageUrl())));
        return responseList;
    }

    @Override
    public LiveProductItemResponse getLiveProduct(Long productId) {
        LiveProductItemResponse response = liveProductMapper.selectByProductId(productId);
        if (response != null) {
            response.setImageUrl(awsUtils.generateAccessUrl(response.getImageUrl()));
        }
        return response;
    }
}
