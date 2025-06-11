package com.yixuan.yh.live.request;

import lombok.Data;

@Data
public class UnpublishRequest {
    /**
     * 动作类型（on_unpublish）
     */
    private String action;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 推流客户端IP
     */
    private String ip;

    /**
     * 虚拟主机（默认Vhost）
     */
    private String vhost;

    /**
     * 应用名
     */
    private String app;

    /**
     * 流名称
     */
    private String stream;

    /**
     * URL参数（可选）
     */
    private String param;

    /**
     * SRS工作目录
     */
    private String cwd;

    /**
     * SRS可执行文件路径
     */
    private String file;
}