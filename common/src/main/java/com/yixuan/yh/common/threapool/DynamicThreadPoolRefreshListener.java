package com.yixuan.yh.common.threapool;

import com.yixuan.yh.common.properties.DynamicThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class DynamicThreadPoolRefreshListener {

    private final DynamicThreadPoolRegistry registry;
    private final DynamicThreadPoolProperties properties;

    public DynamicThreadPoolRefreshListener(DynamicThreadPoolRegistry registry, DynamicThreadPoolProperties properties) {
        this.registry = registry;
        this.properties = properties;
    }

    @EventListener
    public void onEnvironmentChange(EnvironmentChangeEvent event) {
        boolean hasChanged = event.getKeys().stream().anyMatch(key -> key.contains("dynamic.threadpools"));
        if (hasChanged) {
            refresh();
        }
    }

    private void refresh() {
        properties.getThreadpools().forEach((poolName, newParams) -> {
            ThreadPoolExecutor executor = registry.get(poolName);
            if (executor == null) {
                return;
            }

            //  刷新核心和最大线程数（注意顺序，防止 newMax < oldCore 抛异常）
            int oldCore = executor.getCorePoolSize();
            int oldMax = executor.getMaximumPoolSize();
            
            if (newParams.getMaximumPoolSize() >= executor.getCorePoolSize()) {
                executor.setMaximumPoolSize(newParams.getMaximumPoolSize());
                executor.setCorePoolSize(newParams.getCorePoolSize());
            } else {
                executor.setCorePoolSize(newParams.getCorePoolSize());
                executor.setMaximumPoolSize(newParams.getMaximumPoolSize());
            }

            // 刷新存活时间
            executor.setKeepAliveTime(newParams.getKeepAliveTime(), java.util.concurrent.TimeUnit.SECONDS);

            if (oldCore != newParams.getCorePoolSize() || oldMax != newParams.getMaximumPoolSize()) {
                log.warn("线程池 [{}] 参数更新成功! Core: {}->{}, Max: {}->{}",
                        poolName, oldCore, newParams.getCorePoolSize(), oldMax, newParams.getMaximumPoolSize());
            }
        });
    }
}