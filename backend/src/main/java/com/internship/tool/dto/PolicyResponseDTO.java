package com.internship.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO returned by policy endpoints.
 */
@Data
public class PolicyResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(example = "1", description = "Unique policy identifier (database ID)")
    private Long id;

    @Schema(example = "POL1001", description = "Unique policy number")
    private String policyNumber;

    @Schema(example = "Health Insurance Premium - POL1001", description = "Human-readable policy name")
    private String policyName;

    @Schema(example = "Health", description = "Type of policy (Health, Vehicle, Travel, Life, Home)")
    private String policyType;

    @Schema(example = "5000", description = "Premium amount in currency")
    private BigDecimal premiumAmount;

    @Schema(example = "2024-01-01", description = "Policy start date (yyyy-MM-dd)")
    private LocalDate startDate;

    @Schema(example = "2025-01-01", description = "Policy end date (yyyy-MM-dd)")
    private LocalDate endDate;

    @Schema(example = "ACTIVE", description = "Current policy status (ACTIVE, EXPIRED, CANCELLED, PENDING)")
    private String status;
}
