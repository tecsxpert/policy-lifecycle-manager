package com.internship.tool.service;

import com.internship.tool.dto.PolicyRequest;
import com.internship.tool.dto.PolicyResponse;
import com.internship.tool.entity.Policy;
import com.internship.tool.exception.PolicyNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.util.InputSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private EmailService emailService;

    @Cacheable(value = "policies", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<PolicyResponse> getAllPolicies(Pageable pageable) {
        return policyRepository.findByIsDeletedFalse(pageable)
                .map(this::toResponse);
    }

    @Cacheable(value = "policies", key = "#id")
    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(Long id) {
        Policy policy = policyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        return toResponse(policy);
    }

    @CacheEvict(value = {"policies", "policyStats"}, allEntries = true)
    public PolicyResponse createPolicy(PolicyRequest request) {
        validatePolicyRequest(request);
        Policy policy = new Policy();
        policy.setPolicyName(InputSanitizer.sanitize(request.getPolicyName()));
        policy.setPolicyType(InputSanitizer.sanitize(request.getPolicyType()));
        policy.setStatus(InputSanitizer.sanitize(request.getStatus()));
        policy.setPolicyHolder(InputSanitizer.sanitize(request.getPolicyHolder()));
        policy.setExpiryDate(request.getExpiryDate());
        policy.setIsDeleted(false);

        Policy saved = policyRepository.save(policy);
        logger.info("Created policy id={} name={}", saved.getId(), saved.getPolicyName());

        // Async email notification (graceful if mail not configured)
        emailService.sendPolicyCreatedEmail("admin@policy.local", saved.getPolicyName(), saved.getPolicyHolder());

        return toResponse(saved);
    }

    @CacheEvict(value = {"policies", "policyStats"}, allEntries = true)
    public PolicyResponse updatePolicy(Long id, PolicyRequest request) {
        validatePolicyRequest(request);
        Policy policy = policyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        policy.setPolicyName(InputSanitizer.sanitize(request.getPolicyName()));
        policy.setPolicyType(InputSanitizer.sanitize(request.getPolicyType()));
        policy.setStatus(InputSanitizer.sanitize(request.getStatus()));
        policy.setPolicyHolder(InputSanitizer.sanitize(request.getPolicyHolder()));
        policy.setExpiryDate(request.getExpiryDate());

        Policy saved = policyRepository.save(policy);
        logger.info("Updated policy id={}", saved.getId());
        return toResponse(saved);
    }

    @CacheEvict(value = {"policies", "policyStats"}, allEntries = true)
    public void softDeletePolicy(Long id) {
        Policy policy = policyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.setIsDeleted(true);
        policy.setStatus("DELETED");
        policyRepository.save(policy);
        logger.info("Soft-deleted policy id={}", id);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> searchPolicies(String query) {
        return policyRepository.searchByNameOrHolder(InputSanitizer.sanitize(query))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "policyStats")
    @Transactional(readOnly = true)
    public PolicyStats getPolicyStats() {
        long total = policyRepository.countByIsDeletedFalse();
        long active = policyRepository.countByStatusAndIsDeletedFalse("Active");
        return new PolicyStats(total, active);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllActivePoliciesForExport() {
        return policyRepository.findByIsDeletedFalse(Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validatePolicyRequest(PolicyRequest request) {
        if (request == null) {
            throw new ValidationException("Policy request cannot be null");
        }
        if (request.getPolicyName() == null || request.getPolicyName().isBlank()) {
            throw new ValidationException("Policy name is required");
        }
        if (request.getPolicyType() == null || request.getPolicyType().isBlank()) {
            throw new ValidationException("Policy type is required");
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ValidationException("Status is required");
        }
        if (request.getPolicyHolder() == null || request.getPolicyHolder().isBlank()) {
            throw new ValidationException("Policy holder is required");
        }
        if (request.getExpiryDate() != null && request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Expiry date cannot be in the past");
        }
    }

    private PolicyResponse toResponse(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.setId(policy.getId());
        response.setPolicyName(policy.getPolicyName());
        response.setPolicyType(policy.getPolicyType());
        response.setStatus(policy.getStatus());
        response.setPolicyHolder(policy.getPolicyHolder());
        response.setExpiryDate(policy.getExpiryDate());
        response.setIsDeleted(policy.getIsDeleted());
        response.setCreatedAt(policy.getCreatedAt());
        response.setUpdatedAt(policy.getUpdatedAt());
        return response;
    }

    public static class PolicyStats {
        private final long totalPolicies;
        private final long totalActivePolicies;

        public PolicyStats(long totalPolicies, long totalActivePolicies) {
            this.totalPolicies = totalPolicies;
            this.totalActivePolicies = totalActivePolicies;
        }

        public long getTotalPolicies() {
            return totalPolicies;
        }

        public long getTotalActivePolicies() {
            return totalActivePolicies;
        }
    }
}

