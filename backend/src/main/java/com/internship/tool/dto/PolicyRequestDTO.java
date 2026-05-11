package com.internship.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used to receive policy create/update requests.
 */
@Data
public class PolicyRequestDTO {

    @Schema(example = "POL1001", description = "Unique policy number")
    private String policyNumber;

    @Schema(example = "Health Insurance Premium", description = "Human-readable policy name")
    private String policyName;

    @Schema(example = "Health", description = "Type of policy (Health, Vehicle, Travel, Life, Home)")
    private String policyType;

    @Schema(example = "5000", description = "Premium amount in currency")
    private BigDecimal premiumAmount;

    @Schema(example = "2024-01-01", description = "Policy start date (yyyy-MM-dd)")
    private LocalDate startDate;

    @Schema(example = "2025-01-01", description = "Policy end date (yyyy-MM-dd)")
    private LocalDate endDate;

    @Schema(example = "ACTIVE", description = "Policy status (ACTIVE, EXPIRED, CANCELLED, PENDING)")
    private String status;
}
