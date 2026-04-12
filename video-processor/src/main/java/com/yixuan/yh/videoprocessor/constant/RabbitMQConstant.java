package com.yixuan.yh.videoprocessor.constant;

public class RabbitMQConstant {
    /**
     * Interaction
     */
    public final static String VIDEO_INTERACTION_TOPIC_EXCHANGE = "video.interaction.topic";
    public final static String VIDEO_LIKE_QUEUE = "video.like.queue";
    public final static String VIDEO_LIKE_QUEUE_KEY = "video.interaction.like";
    public final static String VIDEO_FAVORITE_QUEUE = "video.favorite.queue";
    public final static String VIDEO_FAVORITE_QUEUE_KEY = "video.interaction.favorite";
    public final static String VIDEO_COMMENT_QUEUE = "video.comment.queue";
    public final static String VIDEO_COMMENT_QUEUE_KEY = "video.interaction.comment";
    public final static String VIDEO_INTERACTION_QUEUE = "Video.interaction.queue";
    public final static String VIDEO_INTERACTION_QUEUE_KEY = "Video.interaction.*";

    public final static String VIDEO_COMMENT_DIRECT_EXCHANGE = "video.comment.direct";
    public final static String VIDEO_COMMENT_INCR_QUEUE = "video.comment.incr.queue";
}