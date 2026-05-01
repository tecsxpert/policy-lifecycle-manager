package com.internship.tool.service;

import com.internship.tool.dto.PolicyRequest;
import com.internship.tool.dto.PolicyResponse;
import com.internship.tool.entity.Policy;
import com.internship.tool.exception.PolicyNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    private PolicyRequest validRequest;
    private Policy existingPolicy;

    @BeforeEach
    void setUp() {
        // Create a no-op EmailService to avoid NPE during createPolicy
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(policyService, "emailService", emailService);

        validRequest = new PolicyRequest();
        validRequest.setPolicyName("Test Policy");
        validRequest.setPolicyType("Auto Insurance");
        validRequest.setStatus("Active");
        validRequest.setPolicyHolder("John Doe");
        validRequest.setExpiryDate(LocalDate.now().plusMonths(6));

        existingPolicy = new Policy();
        existingPolicy.setId(1L);
        existingPolicy.setPolicyName("Old Name");
        existingPolicy.setPolicyType("Auto Insurance");
        existingPolicy.setStatus("Active");
        existingPolicy.setPolicyHolder("John Doe");
        existingPolicy.setExpiryDate(LocalDate.now().plusMonths(3));
        existingPolicy.setIsDeleted(false);
    }

    @Test
    void testGetAllPolicies_ReturnsMappedResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Policy> page = new PageImpl<>(List.of(existingPolicy));
        when(policyRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Page<PolicyResponse> result = policyService.getAllPolicies(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPolicyName()).isEqualTo("Old Name");
    }

    @Test
    void testGetPolicyById_Found_ReturnsResponse() {
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingPolicy));

        PolicyResponse result = policyService.getPolicyById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPolicyName()).isEqualTo("Old Name");
    }

    @Test
    void testGetPolicyById_NotFound_ThrowsException() {
        when(policyRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.getPolicyById(99L))
                .isInstanceOf(PolicyNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void testCreatePolicy_ValidRequest_SavesAndReturnsResponse() {
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> {
            Policy p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PolicyResponse result = policyService.createPolicy(validRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPolicyName()).isEqualTo("Test Policy");
    }

    @Test
    void testCreatePolicy_NullRequest_ThrowsValidationException() {
        assertThatThrownBy(() -> policyService.createPolicy(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void testCreatePolicy_BlankName_ThrowsValidationException() {
        validRequest.setPolicyName("   ");
        assertThatThrownBy(() -> policyService.createPolicy(validRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Policy name is required");
    }

    @Test
    void testCreatePolicy_PastExpiryDate_ThrowsValidationException() {
        validRequest.setExpiryDate(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> policyService.createPolicy(validRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("past");
    }

    @Test
    void testUpdatePolicy_ValidRequest_UpdatesAndReturnsResponse() {
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(existingPolicy);

        PolicyResponse result = policyService.updatePolicy(1L, validRequest);

        assertThat(result.getPolicyName()).isEqualTo("Test Policy");
    }

    @Test
    void testUpdatePolicy_NotFound_ThrowsException() {
        when(policyRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.updatePolicy(99L, validRequest))
                .isInstanceOf(PolicyNotFoundException.class);
    }

    @Test
    void testSoftDeletePolicy_Found_SetsDeletedFlag() {
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(existingPolicy);

        policyService.softDeletePolicy(1L);

        assertThat(existingPolicy.getIsDeleted()).isTrue();
        assertThat(existingPolicy.getStatus()).isEqualTo("DELETED");
    }

    @Test
    void testSoftDeletePolicy_NotFound_ThrowsException() {
        when(policyRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.softDeletePolicy(99L))
                .isInstanceOf(PolicyNotFoundException.class);
    }

    @Test
    void testSearchPolicies_ReturnsMappedResponses() {
        when(policyRepository.searchByNameOrHolder("john")).thenReturn(List.of(existingPolicy));

        List<PolicyResponse> results = policyService.searchPolicies("john");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPolicyHolder()).isEqualTo("John Doe");
    }

    @Test
    void testGetPolicyStats_ReturnsCorrectCounts() {
        when(policyRepository.countByIsDeletedFalse()).thenReturn(100L);
        when(policyRepository.countByStatusAndIsDeletedFalse("Active")).thenReturn(80L);

        PolicyService.PolicyStats stats = policyService.getPolicyStats();

        assertThat(stats.getTotalPolicies()).isEqualTo(100L);
        assertThat(stats.getTotalActivePolicies()).isEqualTo(80L);
    }

    @Test
    void testGetAllActivePoliciesForExport_ReturnsMappedResponses() {
        Page<Policy> page = new PageImpl<>(List.of(existingPolicy));
        when(policyRepository.findByIsDeletedFalse(Pageable.unpaged())).thenReturn(page);

        List<PolicyResponse> results = policyService.getAllActivePoliciesForExport();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPolicyName()).isEqualTo("Old Name");
    }

    @Test
    void testValidatePolicyRequest_NullPolicyType_ThrowsException() {
        validRequest.setPolicyType(null);
        assertThatThrownBy(() -> policyService.createPolicy(validRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Policy type is required");
    }

    @Test
    void testValidatePolicyRequest_NullStatus_ThrowsException() {
        validRequest.setStatus(null);
        assertThatThrownBy(() -> policyService.createPolicy(validRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Status is required");
    }

    @Test
    void testValidatePolicyRequest_NullPolicyHolder_ThrowsException() {
        validRequest.setPolicyHolder(null);
        assertThatThrownBy(() -> policyService.createPolicy(validRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Policy holder is required");
    }
}

