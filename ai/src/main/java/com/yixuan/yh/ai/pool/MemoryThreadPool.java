package com.yixuan.yh.ai.pool;

import com.yixuan.yh.ai.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
public class MemoryThreadPool {
    private final MemoryService memoryService;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, // core pool size
            10, // maximum pool size
            60L, // keep-alive time
            java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(100)
    );

    public void submit(Long userId, String msg) {
        threadPoolExecutor.submit(() -> memoryService.extractAndStoreMemory(userId, msg));
    }
}
