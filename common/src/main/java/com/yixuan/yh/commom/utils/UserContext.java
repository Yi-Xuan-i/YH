package com.yixuan.yh.commom.utils;

public class UserContext {

    private final static ThreadLocal<Long> threadlocal = new ThreadLocal<>();

    public static Long getUser() {
        return threadlocal.get();
    }

    public static void setUser(Long user) {
        threadlocal.set(user);
    }

    public static void removeUser() {
        threadlocal.remove();
    }
}
