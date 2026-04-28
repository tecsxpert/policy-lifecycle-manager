package com.internship.tool.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "policies")
@Schema(name = "Policy", description = "Insurance policy entity representing a lifecycle-managed policy record")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the policy", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 255)
    @Schema(description = "Name of the insurance policy", example = "AutoShield Premium", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyName;

    @Column(name = "policy_type", nullable = false, length = 100)
    @Schema(description = "Type of insurance policy", example = "Auto Insurance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyType;

    @Column(nullable = false, length = 50)
    @Schema(description = "Current status of the policy", example = "Active", allowableValues = {"Active", "Pending", "COMPLETED", "DELETED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Column(name = "policy_holder", nullable = false, length = 255)
    @Schema(description = "Name of the policy holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyHolder;

    @Column(name = "expiry_date")
    @Schema(description = "Policy expiration date (ISO-8601)", example = "2026-12-31")
    private LocalDate expiryDate;

    @Column(name = "is_deleted", nullable = false)
    @Schema(description = "Soft-delete flag", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    @Schema(description = "Timestamp when the policy was created", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Schema(description = "Timestamp when the policy was last updated", example = "2024-06-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    public Policy() {
    }

    public Policy(Long id, String policyName, String policyType, String status, String policyHolder,
                  LocalDate expiryDate, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.policyName = policyName;
        this.policyType = policyType;
        this.status = status;
        this.policyHolder = policyHolder;
        this.expiryDate = expiryDate;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return Objects.equals(id, policy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", policyName='" + policyName + '\'' +
                ", policyType='" + policyType + '\'' +
                ", status='" + status + '\'' +
                ", policyHolder='" + policyHolder + '\'' +
                ", expiryDate=" + expiryDate +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

