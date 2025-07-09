package com.yixuan.yh.video.template;

import com.yixuan.yh.video.mq.VideoInteractionMessage;
import com.yixuan.yh.video.pojo._enum.InteractionStatus;
import org.springframework.aop.framework.AopContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class InteractionTemplate {

    public void handle(Long userId, Long videoId, InteractionStatus status) throws Exception {
        Lock lock = getLock(userId);
        boolean isLock = lock.tryLock(2, TimeUnit.SECONDS);
        if (isLock) {
            try {
                ((InteractionTemplate) AopContext.currentProxy()).tryInteract(userId, videoId, status);
            } finally {
                lock.unlock();
            }
        } else {
            throw new Exception("服务器繁忙，请重试！");
        }
        /*
         * 如果把数据库操作和发送消息队列放在一个事务，则可能出现数据库操作回滚了，但是消息还是成功发送了（但这种目前业务影响不是很大，因为点赞数、收藏数这种不精确影响不是很大，而且可以有修复方案）
         * 如果只把数据库操作放在一个事务，等事务结束后再发送消息，这就可能事务成功了，但是消息未能成功发送。（同上）
         */
        // 构建消息
        VideoInteractionMessage videoInteractionMessage = new VideoInteractionMessage();
        videoInteractionMessage.setUserId(userId);
        videoInteractionMessage.setVideoId(videoId);
        videoInteractionMessage.setStatus(status);
        // 发送到队列异步处理
        sendMessageToQueue(videoInteractionMessage);
    }

    protected abstract Lock getLock(Long userId);

    @Transactional
    protected abstract void tryInteract(Long userId, Long videoId, InteractionStatus interactionStatus) throws Exception;

    protected abstract void sendMessageToQueue(VideoInteractionMessage videoInteractionMessage);
}
