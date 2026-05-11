package com.internship.tool.service;

import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;
import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;
import com.internship.tool.exception.InvalidRequestException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private PolicyRequestDTO sampleRequest;
    private Policy existingPolicy;

    @BeforeEach
    void setUp() {
        sampleRequest = new PolicyRequestDTO();
        sampleRequest.setPolicyNumber("POL1001");
        sampleRequest.setPolicyName("Health Insurance Premium");
        sampleRequest.setPolicyType("Health");
        sampleRequest.setPremiumAmount(BigDecimal.valueOf(5000));
        sampleRequest.setStartDate(LocalDate.of(2024, 1, 1));
        sampleRequest.setEndDate(LocalDate.of(2025, 1, 1));
        sampleRequest.setStatus("ACTIVE");

        existingPolicy = Policy.builder()
                .id(1L)
                .policyNumber("POL1001")
                .policyName("Health Insurance Premium")
                .policyType("Health")
                .premiumAmount(BigDecimal.valueOf(5000))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .status(PolicyStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldReturnAllPolicies() {
        Policy secondPolicy = Policy.builder()
                .id(2L)
                .policyNumber("POL1002")
                .policyName("Car Insurance Cover")
                .policyType("Vehicle")
                .premiumAmount(BigDecimal.valueOf(7500))
                .startDate(LocalDate.of(2023, 6, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .status(PolicyStatus.PENDING)
                .build();

        when(policyRepository.findAll()).thenReturn(List.of(existingPolicy, secondPolicy));

        List<PolicyResponseDTO> policies = policyService.getAllPolicies();

        assertNotNull(policies);
        assertEquals(2, policies.size());
        assertEquals("POL1001", policies.get(0).getPolicyNumber());
        assertEquals("POL1002", policies.get(1).getPolicyNumber());
        assertEquals("Car Insurance Cover", policies.get(1).getPolicyName());

        verify(policyRepository, times(1)).findAll();
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldReturnEmptyListWhenNoPoliciesExist() {
        when(policyRepository.findAll()).thenReturn(Collections.emptyList());

        List<PolicyResponseDTO> policies = policyService.getAllPolicies();

        assertNotNull(policies);
        assertTrue(policies.isEmpty());

        verify(policyRepository, times(1)).findAll();
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldReturnPolicyById() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        PolicyResponseDTO result = policyService.getPolicyById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("POL1001", result.getPolicyNumber());
        assertEquals("Health Insurance Premium", result.getPolicyName());

        verify(policyRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldThrowExceptionWhenPolicyNotFound() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> policyService.getPolicyById(1L));

        verify(policyRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldCreatePolicySuccessfully() {
        Policy savedPolicy = Policy.builder()
                .id(1L)
                .policyNumber(sampleRequest.getPolicyNumber())
                .policyName(sampleRequest.getPolicyName())
                .policyType(sampleRequest.getPolicyType())
                .premiumAmount(sampleRequest.getPremiumAmount())
                .startDate(sampleRequest.getStartDate())
                .endDate(sampleRequest.getEndDate())
                .status(PolicyStatus.ACTIVE)
                .build();

        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        PolicyResponseDTO response = policyService.createPolicy(sampleRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("POL1001", response.getPolicyNumber());
        assertEquals("Health Insurance Premium", response.getPolicyName());

        verify(policyRepository, times(1)).save(any(Policy.class));
        verify(emailService, times(1)).sendPolicyCreatedEmail(anyString(), anyString());
        verifyNoMoreInteractions(policyRepository);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPolicyWithInvalidStatus() {
        PolicyRequestDTO invalidRequest = new PolicyRequestDTO();
        invalidRequest.setPolicyNumber("POL1003");
        invalidRequest.setPolicyName("Invalid Policy");
        invalidRequest.setPolicyType("Health");
        invalidRequest.setPremiumAmount(BigDecimal.valueOf(3000));
        invalidRequest.setStartDate(LocalDate.of(2024, 1, 1));
        invalidRequest.setEndDate(LocalDate.of(2025, 1, 1));
        invalidRequest.setStatus("INVALID_STATUS");

        assertThrows(InvalidRequestException.class, () -> policyService.createPolicy(invalidRequest));

        verify(policyRepository, never()).save(any(Policy.class));
        verify(emailService, never()).sendPolicyCreatedEmail(anyString(), anyString());
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldUpdatePolicySuccessfully() {
        PolicyRequestDTO updateRequest = new PolicyRequestDTO();
        updateRequest.setPolicyNumber("POL1001");
        updateRequest.setPolicyName("Health Insurance Premium Updated");
        updateRequest.setPolicyType("Health");
        updateRequest.setPremiumAmount(BigDecimal.valueOf(5500));
        updateRequest.setStartDate(LocalDate.of(2024, 2, 1));
        updateRequest.setEndDate(LocalDate.of(2025, 2, 1));
        updateRequest.setStatus("EXPIRED");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        Policy updatedPolicy = Policy.builder()
                .id(1L)
                .policyNumber(updateRequest.getPolicyNumber())
                .policyName(updateRequest.getPolicyName())
                .policyType(updateRequest.getPolicyType())
                .premiumAmount(updateRequest.getPremiumAmount())
                .startDate(updateRequest.getStartDate())
                .endDate(updateRequest.getEndDate())
                .status(PolicyStatus.EXPIRED)
                .build();

        when(policyRepository.save(any(Policy.class))).thenReturn(updatedPolicy);

        PolicyResponseDTO result = policyService.updatePolicy(1L, updateRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Health Insurance Premium Updated", result.getPolicyName());
        assertEquals("EXPIRED", result.getStatus());
        assertEquals(BigDecimal.valueOf(5500), result.getPremiumAmount());

        verify(policyRepository, times(1)).findById(1L);
        verify(policyRepository, times(1)).save(any(Policy.class));
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingPolicy() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> policyService.updatePolicy(1L, sampleRequest));

        verify(policyRepository, times(1)).findById(1L);
        verify(policyRepository, never()).save(any(Policy.class));
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPolicyWithInvalidStatus() {
        PolicyRequestDTO invalidRequest = new PolicyRequestDTO();
        invalidRequest.setPolicyNumber("POL1001");
        invalidRequest.setPolicyName("Invalid Update");
        invalidRequest.setPolicyType("Health");
        invalidRequest.setPremiumAmount(BigDecimal.valueOf(5500));
        invalidRequest.setStartDate(LocalDate.of(2024, 2, 1));
        invalidRequest.setEndDate(LocalDate.of(2025, 2, 1));
        invalidRequest.setStatus("WRONG_STATUS");

        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        assertThrows(InvalidRequestException.class, () -> policyService.updatePolicy(1L, invalidRequest));

        verify(policyRepository, times(1)).findById(1L);
        verify(policyRepository, never()).save(any(Policy.class));
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldDeletePolicySuccessfully() {
        when(policyRepository.existsById(1L)).thenReturn(true);

        policyService.deletePolicy(1L);

        verify(policyRepository, times(1)).existsById(1L);
        verify(policyRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(policyRepository, emailService);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingPolicy() {
        when(policyRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> policyService.deletePolicy(1L));

        verify(policyRepository, times(1)).existsById(1L);
        verify(policyRepository, never()).deleteById(1L);
        verifyNoMoreInteractions(policyRepository, emailService);
    }
}
