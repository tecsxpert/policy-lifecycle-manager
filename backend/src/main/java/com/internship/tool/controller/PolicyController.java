package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Updates an existing policy by ID.
     * Restricted to ADMIN and MANAGER roles.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policyDetails) {
        return policyRepository.findById(id)
                .map(policy -> {
                    policy.setPolicyName(policyDetails.getPolicyName());
                    policy.setPolicyType(policyDetails.getPolicyType());
                    policy.setStatus(policyDetails.getStatus());
                    policy.setPolicyHolder(policyDetails.getPolicyHolder());
                    policy.setUpdatedAt(LocalDateTime.now());
                    Policy updatedPolicy = policyRepository.save(policy);
                    return ResponseEntity.ok(updatedPolicy);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Performs a soft delete by updating the policy status to 'DELETED'.
     * Restricted to ADMIN role only.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePolicy(@PathVariable Long id) {
        return policyRepository.findById(id)
                .map(policy -> {
                    policy.setStatus("DELETED");
                    policy.setUpdatedAt(LocalDateTime.now());
                    policyRepository.save(policy);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Searches policies by name or policy holder using a query parameter.
     * Accessible by all authenticated roles.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<List<Policy>> searchPolicies(@RequestParam(name = "q") String q) {
        List<Policy> results = policyRepository.searchByNameOrHolder(q);
        return ResponseEntity.ok(results);
    }

    /**
     * Returns basic KPI stats for policies.
     * Accessible by all authenticated roles.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPolicyStats() {
        long totalPolicies = policyRepository.count();
        long totalActive = policyRepository.countByStatus("Active");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPolicies", totalPolicies);
        stats.put("totalActivePolicies", totalActive);

        return ResponseEntity.ok(stats);
    }
}
