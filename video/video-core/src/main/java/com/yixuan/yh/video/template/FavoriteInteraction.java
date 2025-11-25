package com.yixuan.yh.video.template;

import com.yixuan.yh.common.exception.YHClientException;
import com.yixuan.yh.video.pojo.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import com.yixuan.yh.video.cache.VideoUserFavoriteCache;
import com.yixuan.yh.video.constant.RabbitMQConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteInteraction extends InteractionTemplate {
    private final VideoUserFavoriteCache videoUserFavoriteCache;
    private final RabbitTemplate rabbitTemplate;

    protected void tryInteract(Long userId, Long videoId, InteractionStatus status) {
        // 判断当前操作是否合法
        if (!((status.equals(InteractionStatus.FRONT) && videoUserFavoriteCache.tryFavorite(userId, videoId)) ||
                (status.equals(InteractionStatus.BACK) && videoUserFavoriteCache.tryUnFavorite(userId, videoId)))) {
            throw new YHClientException("异常操作！");
        }
    }

    @Override
    protected void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage) {
        // 信息发送到消息队列异步处理
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_FAVORITE_QUEUE, videoInteractionMessage);
    }
}
