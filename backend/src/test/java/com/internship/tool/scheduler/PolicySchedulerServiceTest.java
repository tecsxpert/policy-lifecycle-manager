package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicySchedulerServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicySchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles initialization
    }

    @Test
    void testCheckOverduePolicies_NoOverduePolicies() {
        when(policyRepository.findByStatusNotAndExpiryDateBefore("COMPLETED", LocalDate.now()))
                .thenReturn(Collections.emptyList());

        assertThatNoException().isThrownBy(() -> schedulerService.checkOverduePolicies());
        verify(policyRepository, times(1)).findByStatusNotAndExpiryDateBefore("COMPLETED", LocalDate.now());
    }

    @Test
    void testCheckOverduePolicies_WithOverduePolicies() {
        Policy overdue = new Policy();
        overdue.setPolicyName("Overdue Policy");
        overdue.setExpiryDate(LocalDate.now().minusDays(5));

        when(policyRepository.findByStatusNotAndExpiryDateBefore("COMPLETED", LocalDate.now()))
                .thenReturn(List.of(overdue));

        assertThatNoException().isThrownBy(() -> schedulerService.checkOverduePolicies());
        verify(policyRepository, times(1)).findByStatusNotAndExpiryDateBefore("COMPLETED", LocalDate.now());
    }

    @Test
    void testCheckExpiringSoonPolicies_NoPolicies() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        when(policyRepository.findByExpiryDate(sevenDaysFromNow))
                .thenReturn(Collections.emptyList());

        assertThatNoException().isThrownBy(() -> schedulerService.checkExpiringSoonPolicies());
        verify(policyRepository, times(1)).findByExpiryDate(sevenDaysFromNow);
    }

    @Test
    void testCheckExpiringSoonPolicies_WithPolicies() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        Policy expiring = new Policy();
        expiring.setPolicyName("Expiring Soon");
        expiring.setExpiryDate(sevenDaysFromNow);

        when(policyRepository.findByExpiryDate(sevenDaysFromNow))
                .thenReturn(List.of(expiring));

        assertThatNoException().isThrownBy(() -> schedulerService.checkExpiringSoonPolicies());
        verify(policyRepository, times(1)).findByExpiryDate(sevenDaysFromNow);
    }

    @Test
    void testGenerateWeeklySummary() {
        when(policyRepository.count()).thenReturn(100L);
        when(policyRepository.countByStatus("Active")).thenReturn(80L);
        when(policyRepository.countByStatus("Pending")).thenReturn(20L);

        assertThatNoException().isThrownBy(() -> schedulerService.generateWeeklySummary());
        verify(policyRepository, times(1)).count();
        verify(policyRepository, times(1)).countByStatus("Active");
        verify(policyRepository, times(1)).countByStatus("Pending");
    }
}

