package com.yixuan.yh.video.constant;

public class RabbitMQConstant {
    public final static String VIDEO_POST_QUEUE = "video.post.queue";
    public final static String VIDEO_LIKE_QUEUE = "video.like.queue";
    public final static String VIDEO_FAVORITE_QUEUE = "video.favorite.queue";

    public final static String VIDEO_COMMENT_FANOUT_EXCHANGE = "video.comment.fanout";
    public final static String VIDEO_COMMENT_COUNT_QUEUE = "video.comment.count.queue";
}
