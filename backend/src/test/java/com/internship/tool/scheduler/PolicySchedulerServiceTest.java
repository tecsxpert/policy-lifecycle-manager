package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicySchedulerServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicySchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        // Inject a real no-op EmailService to avoid NPE in scheduler methods
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(schedulerService, "emailService", emailService);
    }

    @Test
    void testCheckOverduePolicies_NoOverduePolicies() {
        when(policyRepository.findByStatusNotAndExpiryDateBefore(eq("COMPLETED"), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        assertThatNoException().isThrownBy(() -> schedulerService.checkOverduePolicies());
        verify(policyRepository, times(1)).findByStatusNotAndExpiryDateBefore(eq("COMPLETED"), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void testCheckOverduePolicies_WithOverduePolicies() {
        Policy overdue = new Policy();
        overdue.setPolicyName("Overdue Policy");
        overdue.setExpiryDate(LocalDate.now().minusDays(5));

        when(policyRepository.findByStatusNotAndExpiryDateBefore(eq("COMPLETED"), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(List.of(overdue));

        assertThatNoException().isThrownBy(() -> schedulerService.checkOverduePolicies());
        verify(policyRepository, times(1)).findByStatusNotAndExpiryDateBefore(eq("COMPLETED"), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void testCheckExpiringSoonPolicies_NoPolicies() {
        when(policyRepository.findByExpiryDate(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        assertThatNoException().isThrownBy(() -> schedulerService.checkExpiringSoonPolicies());
        verify(policyRepository, times(1)).findByExpiryDate(any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void testCheckExpiringSoonPolicies_WithPolicies() {
        Policy expiring = new Policy();
        expiring.setPolicyName("Expiring Soon");
        expiring.setExpiryDate(LocalDate.now().plusDays(7));

        when(policyRepository.findByExpiryDate(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(List.of(expiring));

        assertThatNoException().isThrownBy(() -> schedulerService.checkExpiringSoonPolicies());
        verify(policyRepository, times(1)).findByExpiryDate(any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void testGenerateWeeklySummary() {
        when(policyRepository.countByIsDeletedFalse()).thenReturn(100L);
        when(policyRepository.countByStatusAndIsDeletedFalse("Active")).thenReturn(80L);
        when(policyRepository.countByStatusAndIsDeletedFalse("Pending")).thenReturn(20L);

        assertThatNoException().isThrownBy(() -> schedulerService.generateWeeklySummary());
        verify(policyRepository, times(1)).countByIsDeletedFalse();
        verify(policyRepository, times(1)).countByStatusAndIsDeletedFalse("Active");
        verify(policyRepository, times(1)).countByStatusAndIsDeletedFalse("Pending");
    }
}

