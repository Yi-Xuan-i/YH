package com.yixuan.yh.common.threapool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class DynamicThreadPoolRegistry {
    private final Map<String, ThreadPoolExecutor> HOLDER = new ConcurrentHashMap<>();

    public void register(String name, ThreadPoolExecutor executor) {
        HOLDER.put(name, executor);
    }

    public ThreadPoolExecutor get(String name) {
        return HOLDER.get(name);
    }

    public Map<String, ThreadPoolExecutor> getAll() {
        return HOLDER;
    }
}
