package com.yixuan.yh.admin.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Audit {
    private Long id;
    private Long adminId;
    private String requestMethod;
    private String requestPath;
    private String requestBody;
    private LocalDateTime createdTime;

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
