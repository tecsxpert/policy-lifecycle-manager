package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PolicyRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById() {
        Policy policy = buildPolicy("AutoShield", "Auto Insurance", "Active", "John Doe", LocalDate.now().plusMonths(6));
        Policy saved = policyRepository.save(policy);

        Optional<Policy> found = policyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPolicyName()).isEqualTo("AutoShield");
    }

    @Test
    void testSearchByNameOrHolder() {
        policyRepository.save(buildPolicy("HomeGuard", "Home Insurance", "Active", "Alice Smith", LocalDate.now().plusMonths(12)));
        policyRepository.save(buildPolicy("LifeSecure", "Life Insurance", "Pending", "Bob Jones", LocalDate.now().plusMonths(24)));
        policyRepository.save(buildPolicy("HealthFirst", "Health Insurance", "Active", "Alice Smith", LocalDate.now().plusMonths(6)));

        List<Policy> results = policyRepository.searchByNameOrHolder("alice");
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Policy::getPolicyName).contains("HomeGuard", "HealthFirst");
    }

    @Test
    void testFindByStatus() {
        policyRepository.save(buildPolicy("Policy A", "Auto", "Active", "Holder A", LocalDate.now().plusMonths(6)));
        policyRepository.save(buildPolicy("Policy B", "Auto", "Pending", "Holder B", LocalDate.now().plusMonths(6)));
        policyRepository.save(buildPolicy("Policy C", "Auto", "Active", "Holder C", LocalDate.now().plusMonths(6)));

        List<Policy> activePolicies = policyRepository.findByStatus("Active");
        assertThat(activePolicies).hasSize(2);
    }

    @Test
    void testFindByCreatedAtBetween() {
        LocalDateTime now = LocalDateTime.now();
        Policy policy = buildPolicy("DateRange", "Auto", "Active", "Holder", LocalDate.now().plusMonths(6));
        policyRepository.save(policy);

        List<Policy> results = policyRepository.findByCreatedAtBetween(now.minusDays(1), now.plusDays(1));
        assertThat(results).hasSize(1);
    }

    @Test
    void testCountByStatus() {
        policyRepository.save(buildPolicy("P1", "Auto", "Active", "H1", LocalDate.now().plusMonths(6)));
        policyRepository.save(buildPolicy("P2", "Auto", "Active", "H2", LocalDate.now().plusMonths(6)));
        policyRepository.save(buildPolicy("P3", "Auto", "Pending", "H3", LocalDate.now().plusMonths(6)));

        assertThat(policyRepository.countByStatus("Active")).isEqualTo(2);
        assertThat(policyRepository.countByStatus("Pending")).isEqualTo(1);
    }

    @Test
    void testFindByStatusNotAndExpiryDateBefore() {
        LocalDate today = LocalDate.now();
        policyRepository.save(buildPolicy("Overdue1", "Auto", "Active", "H1", today.minusDays(5)));
        policyRepository.save(buildPolicy("Overdue2", "Auto", "Pending", "H2", today.minusDays(10)));
        policyRepository.save(buildPolicy("NotOverdue", "Auto", "COMPLETED", "H3", today.minusDays(1)));
        policyRepository.save(buildPolicy("Future", "Auto", "Active", "H4", today.plusDays(30)));

        List<Policy> overdue = policyRepository.findByStatusNotAndExpiryDateBefore("COMPLETED", today);
        assertThat(overdue).hasSize(2);
    }

    @Test
    void testFindByExpiryDate() {
        LocalDate targetDate = LocalDate.now().plusDays(7);
        policyRepository.save(buildPolicy("ExpiringSoon", "Auto", "Active", "H1", targetDate));
        policyRepository.save(buildPolicy("NotExpiring", "Auto", "Active", "H2", targetDate.plusDays(1)));

        List<Policy> results = policyRepository.findByExpiryDate(targetDate);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPolicyName()).isEqualTo("ExpiringSoon");
    }

    @Test
    void testFindByIsDeletedFalse() {
        policyRepository.save(buildPolicy("Active1", "Auto", "Active", "H1", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("Active2", "Auto", "Active", "H2", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("Deleted", "Auto", "DELETED", "H3", LocalDate.now().plusMonths(6), true));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Policy> page = policyRepository.findByIsDeletedFalse(pageable);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindByIdAndIsDeletedFalse() {
        Policy active = policyRepository.save(buildPolicy("ActivePolicy", "Auto", "Active", "H1", LocalDate.now().plusMonths(6), false));
        Policy deleted = policyRepository.save(buildPolicy("DeletedPolicy", "Auto", "DELETED", "H2", LocalDate.now().plusMonths(6), true));

        Optional<Policy> foundActive = policyRepository.findByIdAndIsDeletedFalse(active.getId());
        assertThat(foundActive).isPresent();

        Optional<Policy> foundDeleted = policyRepository.findByIdAndIsDeletedFalse(deleted.getId());
        assertThat(foundDeleted).isEmpty();
    }

    @Test
    void testCountByIsDeletedFalse() {
        policyRepository.save(buildPolicy("A1", "Auto", "Active", "H1", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("A2", "Auto", "Active", "H2", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("D1", "Auto", "DELETED", "H3", LocalDate.now().plusMonths(6), true));

        assertThat(policyRepository.countByIsDeletedFalse()).isEqualTo(2);
    }

    @Test
    void testCountByStatusAndIsDeletedFalse() {
        policyRepository.save(buildPolicy("A1", "Auto", "Active", "H1", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("A2", "Auto", "Active", "H2", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("P1", "Auto", "Pending", "H3", LocalDate.now().plusMonths(6), false));
        policyRepository.save(buildPolicy("D1", "Auto", "Active", "H4", LocalDate.now().plusMonths(6), true));

        assertThat(policyRepository.countByStatusAndIsDeletedFalse("Active")).isEqualTo(2);
        assertThat(policyRepository.countByStatusAndIsDeletedFalse("Pending")).isEqualTo(1);
    }

    private Policy buildPolicy(String name, String type, String status, String holder, LocalDate expiry) {
        return buildPolicy(name, type, status, holder, expiry, false);
    }

    private Policy buildPolicy(String name, String type, String status, String holder, LocalDate expiry, boolean deleted) {
        Policy p = new Policy();
        p.setPolicyName(name);
        p.setPolicyType(type);
        p.setStatus(status);
        p.setPolicyHolder(holder);
        p.setExpiryDate(expiry);
        p.setIsDeleted(deleted);
        return p;
    }
}

