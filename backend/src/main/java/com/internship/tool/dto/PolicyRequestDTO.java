package com.internship.tool.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used to receive policy create/update requests.
 */
@Data
public class PolicyRequestDTO {
    private String policyNumber;
    private String policyName;
    private String policyType;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
