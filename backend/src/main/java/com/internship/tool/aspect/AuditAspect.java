package com.internship.tool.aspect;

import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.Policy;
import com.internship.tool.repository.AuditRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditRepository auditRepository;

    /**
     * Intercepts all methods in PolicyController after they return successfully.
     * Creates an audit log entry for create, update, and delete operations.
     */
    @AfterReturning(pointcut = "execution(* com.internship.tool.controller.PolicyController.*(..))", returning = "result")
    public void auditPolicyAction(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String action = null;
        Long entityId = null;

        if ("createPolicy".equals(methodName)) {
            action = "POLICY_CREATED";
            entityId = extractPolicyIdFromResult(result);
        } else if ("updatePolicy".equals(methodName)) {
            action = "POLICY_UPDATED";
            entityId = extractPolicyIdFromResult(result);
            if (entityId == null && args.length > 0 && args[0] instanceof Long) {
                entityId = (Long) args[0];
            }
        } else if ("softDeletePolicy".equals(methodName)) {
            action = "POLICY_DELETED";
            if (args.length > 0 && args[0] instanceof Long) {
                entityId = (Long) args[0];
            }
        } else {
            // Skip read-only / search / stats methods
            return;
        }

        String changedBy = getCurrentUsername();

        AuditLog log = new AuditLog();
        log.setEntityName("Policy");
        log.setEntityId(entityId);
        log.setAction(action);
        log.setChangedBy(changedBy);
        log.setChangeDate(LocalDateTime.now());

        auditRepository.save(log);
    }

    private Long extractPolicyIdFromResult(Object result) {
        if (result instanceof ResponseEntity<?> response && response.getBody() instanceof Policy policy) {
            return policy.getId();
        }
        return null;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}
