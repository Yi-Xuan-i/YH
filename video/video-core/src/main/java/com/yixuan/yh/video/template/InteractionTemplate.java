package com.yixuan.yh.video.template;

import com.yixuan.yh.video.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;

import java.util.concurrent.locks.Lock;

public abstract class InteractionTemplate {

    public void handle(Long userId, Long videoId, InteractionStatus status) throws Exception {
        tryInteract(userId, videoId, status);
        /*
         * 如果把数据库操作和发送消息队列放在一个事务，则可能出现数据库操作回滚了，但是消息还是成功发送了。
         * 如果只把数据库操作放在一个事务，等事务结束后再发送消息，这就可能事务成功了，但是消息未能成功发送。
         * 但是点赞记录丢失其实影响并不会很大（至于多出的点赞数，可以定时对账）。
         */
        // 构建消息
        VideoInteractionMessage videoInteractionMessage = new VideoInteractionMessage();
        videoInteractionMessage.setUserId(userId);
        videoInteractionMessage.setVideoId(videoId);
        videoInteractionMessage.setStatus(status);
        // 发送到队列异步处理
        sendMessageToQueue(videoInteractionMessage);
    }

    protected abstract void tryInteract(Long userId, Long videoId, InteractionStatus interactionStatus) throws Exception;

    protected abstract void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage);
}
