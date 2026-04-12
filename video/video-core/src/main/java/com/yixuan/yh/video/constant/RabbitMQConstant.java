package com.yixuan.yh.video.constant;

public class RabbitMQConstant {
    /**
     * review
     */
    public final static String VIDEO_REVIEW_QUEUE = "video.review.queue";

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
}
