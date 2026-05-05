package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Policy entity
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /**
     * Find policy by policy number
     */
    Optional<Policy> findByPolicyNumber(String policyNumber);
}