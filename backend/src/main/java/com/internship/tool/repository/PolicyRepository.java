package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

       /**
        * Searches policies by a keyword that matches either the policy name or the
        * policy holder.
        * Uses a case-insensitive partial match.
        */
       @Query("SELECT p FROM Policy p WHERE LOWER(p.policyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                     "OR LOWER(p.policyHolder) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       List<Policy> searchByNameOrHolder(@Param("searchTerm") String searchTerm);

       /**
        * Filters policies by their status (e.g., 'Active', 'Pending').
        */
       List<Policy> findByStatus(String status);

       /**
        * Finds policies created within a specific date range.
        */
       @Query("SELECT p FROM Policy p WHERE p.createdAt BETWEEN :startDate AND :endDate")
       List<Policy> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Counts policies by their status for KPI / stats endpoints.
        */
       long countByStatus(String status);

       /**
        * Finds policies with a status other than the given one whose expiry_date is
        * before the provided date.
        * Used by the overdue reminder scheduler.
        */
       List<Policy> findByStatusNotAndExpiryDateBefore(String status, LocalDate expiryDate);

       /**
        * Finds policies with an expiry_date exactly matching the given date.
        * Used by the advance alert scheduler.
        */
       List<Policy> findByExpiryDate(LocalDate expiryDate);
}
