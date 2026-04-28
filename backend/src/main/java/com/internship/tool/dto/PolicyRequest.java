package com.internship.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "PolicyRequest", description = "Request payload for creating or updating a policy")
public class PolicyRequest {

    @NotBlank(message = "Policy name is required")
    @Size(max = 255, message = "Policy name must be at most 255 characters")
    @Schema(description = "Name of the insurance policy", example = "AutoShield Premium", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyName;

    @NotBlank(message = "Policy type is required")
    @Size(max = 100, message = "Policy type must be at most 100 characters")
    @Schema(description = "Type of insurance policy", example = "Auto Insurance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyType;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must be at most 50 characters")
    @Schema(description = "Current status of the policy", example = "Active", allowableValues = {"Active", "Pending", "COMPLETED", "DELETED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @NotBlank(message = "Policy holder is required")
    @Size(max = 255, message = "Policy holder must be at most 255 characters")
    @Schema(description = "Name of the policy holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyHolder;

    @Schema(description = "Policy expiration date (ISO-8601)", example = "2026-12-31")
    private LocalDate expiryDate;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(String policyHolder) {
        this.policyHolder = policyHolder;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}

