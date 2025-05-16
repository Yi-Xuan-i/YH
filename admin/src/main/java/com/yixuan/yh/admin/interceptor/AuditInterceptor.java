package com.yixuan.yh.admin.interceptor;

import com.yixuan.yh.admin.entity.Audit;
import com.yixuan.yh.admin.service.AuditService;
import com.yixuan.yh.commom.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditService auditService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Audit audit = new Audit();
        audit.setAdminId(UserContext.getUser());
        audit.setRequestMethod(request.getMethod());
        audit.setRequestPath(request.getServletPath());
        audit.setRequestBody(getRequestBody(request));
        auditService.addRecord(audit);
    }

    public static String getRequestBody(HttpServletRequest request) {
        if (request.getContentLength() <= 0  || request.getContentType() == null || !request.getContentType().equals(MediaType.APPLICATION_JSON.toString())) {
            return null;
        }

        return ((ContentCachingRequestWrapper) request).getContentAsString();
    }
}
