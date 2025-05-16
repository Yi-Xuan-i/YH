package com.yixuan.yh.admin.service.impl;

import com.yixuan.yh.admin.entity.Audit;
import com.yixuan.yh.admin.mapper.AuditMapper;
import com.yixuan.yh.admin.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditMapper auditMapper;

    @Override
    public void addRecord(Audit audit) {
        auditMapper.insert(audit);
    }

    @Override
    public void deleteAudit(Long id) {
        auditMapper.delete(id);
    }

    @Override
    public List<Audit> getAudits(Long id, Long adminId, String requestMethod, String requestPath, String createdTime) {
        return auditMapper.selectByCond(id, adminId, requestMethod, requestPath, createdTime);
    }

    @Override
    public List<Audit> getAudits(Long lastMaxId) {
        return auditMapper.selectPage(lastMaxId);
    }
}
