package com.yixuan.yh.admin.service;

import com.yixuan.yh.admin.entity.Audit;

import java.util.List;

public interface AuditService {
    void addRecord(Audit audit);

    void deleteAudit(Long id);

    List<Audit> getAudits(Long id, Long adminId, String requestMethod, String requestPath, String createdTime);

    List<Audit> getAudits(Long lastMaxId);
}
