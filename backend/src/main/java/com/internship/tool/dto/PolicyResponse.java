package com.internship.tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "PolicyResponse", description = "Response payload for a policy")
public class PolicyResponse {

    @Schema(description = "Unique identifier for the policy", example = "1")
    private Long id;

    @Schema(description = "Name of the insurance policy", example = "AutoShield Premium")
    private String policyName;

    @Schema(description = "Type of insurance policy", example = "Auto Insurance")
    private String policyType;

    @Schema(description = "Current status of the policy", example = "Active")
    private String status;

    @Schema(description = "Name of the policy holder", example = "John Doe")
    private String policyHolder;

    @Schema(description = "Policy expiration date", example = "2026-12-31")
    private LocalDate expiryDate;

    @Schema(description = "Soft-delete flag", example = "false")
    private Boolean isDeleted;

    @Schema(description = "Timestamp when the policy was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the policy was last updated", example = "2024-06-20T14:45:00")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

