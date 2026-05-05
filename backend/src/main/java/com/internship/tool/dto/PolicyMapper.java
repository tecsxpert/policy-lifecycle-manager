package com.internship.tool.dto;

import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;

/**
 * Simple mapper between Policy entity and DTO representations.
 */
public final class PolicyMapper {

    private PolicyMapper() {
        // Utility class
    }

    public static Policy toEntity(PolicyRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        PolicyStatus policyStatus = toPolicyStatus(requestDTO.getStatus());

        return Policy.builder()
                .policyNumber(requestDTO.getPolicyNumber())
                .policyName(requestDTO.getPolicyName())
                .policyType(requestDTO.getPolicyType())
                .premiumAmount(requestDTO.getPremiumAmount())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .status(policyStatus)
                .build();
    }

    public static PolicyResponseDTO toResponse(Policy policy) {
        if (policy == null) {
            return null;
        }

        PolicyResponseDTO responseDTO = new PolicyResponseDTO();
        responseDTO.setId(policy.getId());
        responseDTO.setPolicyNumber(policy.getPolicyNumber());
        responseDTO.setPolicyName(policy.getPolicyName());
        responseDTO.setPolicyType(policy.getPolicyType());
        responseDTO.setPremiumAmount(policy.getPremiumAmount());
        responseDTO.setStartDate(policy.getStartDate());
        responseDTO.setEndDate(policy.getEndDate());
        responseDTO.setStatus(policy.getStatus() != null ? policy.getStatus().name() : null);
        return responseDTO;
    }

    private static PolicyStatus toPolicyStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Policy status must be provided");
        }

        try {
            return PolicyStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    String.format("Invalid policy status '%s'. Allowed values: %s", status,
                            String.join(", ",
                                    java.util.Arrays.stream(PolicyStatus.values())
                                            .map(Enum::name)
                                            .toList())), ex);
        }
    }
}
