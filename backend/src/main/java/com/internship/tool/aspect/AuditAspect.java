package com.internship.tool.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.dto.PolicyResponse;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.Policy;
import com.internship.tool.repository.AuditRepository;
import com.internship.tool.repository.PolicyRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("execution(* com.internship.tool.controller.PolicyController.*(..))")
    public Object auditPolicyAction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String action = null;
        Long entityId = null;
        String oldValue = null;
        String newValue = null;

        // Capture old state before proceeding
        if ("updatePolicy".equals(methodName) && args.length > 0 && args[0] instanceof Long id) {
            entityId = id;
            oldValue = serializeObject(policyRepository.findByIdAndIsDeletedFalse(id).orElse(null));
        } else if ("softDeletePolicy".equals(methodName) && args.length > 0 && args[0] instanceof Long id) {
            entityId = id;
            oldValue = serializeObject(policyRepository.findByIdAndIsDeletedFalse(id).orElse(null));
        }

        // Proceed with the actual method
        Object result = joinPoint.proceed();

        // Determine action and capture new state
        if ("createPolicy".equals(methodName)) {
            action = "POLICY_CREATED";
            entityId = extractIdFromResult(result);
            newValue = serializeObject(fetchPolicyById(entityId));
        } else if ("updatePolicy".equals(methodName)) {
            action = "POLICY_UPDATED";
            if (entityId == null) {
                entityId = extractIdFromResult(result);
            }
            newValue = serializeObject(fetchPolicyById(entityId));
        } else if ("softDeletePolicy".equals(methodName)) {
            action = "POLICY_DELETED";
            if (entityId == null && args.length > 0 && args[0] instanceof Long) {
                entityId = (Long) args[0];
            }
            newValue = serializeObject(fetchPolicyById(entityId));
        } else {
            return result;
        }

        // Skip audit if we couldn't determine an entity ID (should not happen, but safety check)
        if (entityId == null) {
            logger.warn("Audit skipped: could not determine entityId for method={}", methodName);
            return result;
        }

        String changedBy = getCurrentUsername();

        AuditLog log = new AuditLog();
        log.setEntityName("Policy");
        log.setEntityId(entityId);
        log.setAction(action);
        log.setChangedBy(changedBy);
        log.setChangeDate(LocalDateTime.now());
        log.setOldValue(oldValue);
        log.setNewValue(newValue);

        auditRepository.save(log);
        logger.info("Audit log saved: action={}, entityId={}, changedBy={}", action, entityId, changedBy);

        return result;
    }

    private Policy fetchPolicyById(Long id) {
        if (id == null) return null;
        return policyRepository.findById(id).orElse(null);
    }

    private String serializeObject(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("Failed to serialize object for audit log", e);
            return "{\"error\": \"serialization_failed\"}";
        }
    }

    private Long extractIdFromResult(Object result) {
        if (result instanceof ResponseEntity<?> response && response.getBody() instanceof PolicyResponse dto) {
            return dto.getId();
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
