package com.yixuan.yh.admin.controller;

import com.yixuan.yh.admin.annotations.RequiresPermission;
import com.yixuan.yh.admin.entity.Audit;
import com.yixuan.yh.admin.service.AuditService;
import com.yixuan.yh.common.response.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Audit")
@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    @RequiresPermission({"AUDIT:SELECT"})
    public Result<List<Audit>> getAudits(@RequestParam(required = false) Long id,
                                         @RequestParam(required = false) Long adminId,
                                         @RequestParam(required = false) String requestMethod,
                                         @RequestParam(required = false) String requestPath,
                                         @RequestParam(required = false) String createdTime) {
        return Result.success(auditService.getAudits(id, adminId, requestMethod, requestPath, createdTime));
    }

    @GetMapping("/list")
    @RequiresPermission({"AUDIT:SELECT"})
    public Result<List<Audit>> getAudits(@RequestParam Long lastMaxId) {
        return Result.success(auditService.getAudits(lastMaxId));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission({"AUDIT:DELETE"})
    public Result<Void> deleteAudit(@PathVariable Long id) {
        auditService.deleteAudit(id);
        return Result.success();
    }
}
