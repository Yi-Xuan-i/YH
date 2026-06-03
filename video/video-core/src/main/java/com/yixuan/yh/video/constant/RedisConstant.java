package com.yixuan.yh.video.constant;

public class RedisConstant {
    public final static String VIDEO_USER_LIKE_LOCK_PREFIX = "video:user:like:lock:";
    public final static String VIDEO_USER_LIKE_KEY_PREFIX = "video:user:like:";
    public final static String VIDEO_USER_FAVORITE_LOCK_PREFIX = "video:user:favorite:lock:";
    public final static String VIDEO_USER_FAVORITE_KEY_PREFIX = "video:user:favorite:";

    // InteractionLua
    public enum InteractionLua {
        NOT_EXIST(0L),
        ERROR(1L),
        OK(2L);

        private final long value;

        InteractionLua(long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }
    }
}
