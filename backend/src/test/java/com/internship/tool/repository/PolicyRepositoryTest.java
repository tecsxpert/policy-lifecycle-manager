package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PolicyRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Test
    void shouldSavePolicySuccessfully() {
        Policy policy = Policy.builder()
                .policyNumber("POL2001")
                .policyName("Comprehensive Health Plan")
                .policyType("Health")
                .premiumAmount(BigDecimal.valueOf(8200))
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .status(PolicyStatus.ACTIVE)
                .build();

        Policy saved = policyRepository.save(policy);

        assertNotNull(saved.getId(), "Saved policy should have generated ID");
        assertEquals("POL2001", saved.getPolicyNumber());
    }

    @Test
    void shouldFindPolicyById() {
        Policy policy = Policy.builder()
                .policyNumber("POL2002")
                .policyName("Auto Protection Plan")
                .policyType("Auto")
                .premiumAmount(BigDecimal.valueOf(6200))
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .status(PolicyStatus.PENDING)
                .build();

        Policy saved = policyRepository.save(policy);
        Optional<Policy> fetched = policyRepository.findById(saved.getId());

        assertTrue(fetched.isPresent(), "Policy should be found by ID");
        assertEquals("POL2002", fetched.get().getPolicyNumber());
    }

    @Test
    void shouldReturnAllPolicies() {
        Policy first = Policy.builder()
                .policyNumber("POL2003")
                .policyName("Life Secure Plan")
                .policyType("Life")
                .premiumAmount(BigDecimal.valueOf(9200))
                .startDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2025, 3, 1))
                .status(PolicyStatus.ACTIVE)
                .build();

        Policy second = Policy.builder()
                .policyNumber("POL2004")
                .policyName("Home Shield Plan")
                .policyType("Home")
                .premiumAmount(BigDecimal.valueOf(4300))
                .startDate(LocalDate.of(2024, 5, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .status(PolicyStatus.CANCELLED)
                .build();

        policyRepository.save(first);
        policyRepository.save(second);

        List<Policy> policies = policyRepository.findAll();

        assertEquals(2, policies.size(), "Repository should return both saved policies");
    }

    @Test
    void shouldCheckPolicyExists() {
        Policy policy = Policy.builder()
                .policyNumber("POL2005")
                .policyName("Travel Guard Plan")
                .policyType("Travel")
                .premiumAmount(BigDecimal.valueOf(3100))
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2025, 7, 1))
                .status(PolicyStatus.EXPIRED)
                .build();

        Policy saved = policyRepository.save(policy);

        assertTrue(policyRepository.existsById(saved.getId()), "Policy should exist after save");
    }

    @Test
    void shouldDeletePolicy() {
        Policy policy = Policy.builder()
                .policyNumber("POL2006")
                .policyName("Small Business Plan")
                .policyType("Business")
                .premiumAmount(BigDecimal.valueOf(7600))
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2025, 9, 1))
                .status(PolicyStatus.PENDING)
                .build();

        Policy saved = policyRepository.save(policy);
        policyRepository.deleteById(saved.getId());

        assertFalse(policyRepository.existsById(saved.getId()), "Policy should no longer exist after delete");
    }

    @Test
    void shouldPopulateAuditTimestamps() throws InterruptedException {
        Policy policy = Policy.builder()
                .policyNumber("POL2007")
                .policyName("Audit Test Plan")
                .policyType("Test")
                .premiumAmount(BigDecimal.valueOf(1000))
                .startDate(LocalDate.of(2024, 10, 1))
                .endDate(LocalDate.of(2025, 10, 1))
                .status(PolicyStatus.ACTIVE)
                .build();

        // Save initial policy
        Policy saved = policyRepository.save(policy);

        assertNotNull(saved.getCreatedAt(), "Created timestamp should be populated");
        assertNotNull(saved.getUpdatedAt(), "Updated timestamp should be populated");

        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // Update policy to trigger updatedAt change
        saved.setPolicyName("Updated Audit Test Plan");
        Policy updated = policyRepository.save(saved);

        assertNotNull(updated.getCreatedAt(), "Created timestamp should remain populated");
        assertNotNull(updated.getUpdatedAt(), "Updated timestamp should remain populated");
        assertEquals(saved.getCreatedAt(), updated.getCreatedAt(), "Created timestamp should not change on update");
    }
}
