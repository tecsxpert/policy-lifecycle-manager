package com.internship.tool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Policy Entity
 * Represents a policy in the Policy Lifecycle Manager system.
 * Table: policies
 */
@Entity
@Table(name = "policies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    /**
     * Unique identifier for the policy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique policy number (business key)
     */
    @Column(name = "policy_number", unique = true, nullable = false, length = 100)
    private String policyNumber;

    /**
     * Human-readable policy name
     */
    @Column(name = "policy_name", nullable = false, length = 255)
    private String policyName;

    /**
     * Type of policy (e.g., Health, Auto, Home, Life)
     */
    @Column(name = "policy_type", nullable = false, length = 50)
    private String policyType;

    /**
     * Premium amount (using BigDecimal for precision)
     */
    @Column(name = "premium_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumAmount;

    /**
     * Policy effective start date
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Policy expiration/end date
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Current status of the policy
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PolicyStatus status;

    /**
     * Record creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;
}