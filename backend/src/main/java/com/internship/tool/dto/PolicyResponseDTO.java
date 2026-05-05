package com.internship.tool.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO returned by policy endpoints.
 */
@Data
public class PolicyResponseDTO {
    private Long id;
    private String policyNumber;
    private String policyName;
    private String policyType;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
