package com.internship.tool.repository;

import com.internship.tool.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p WHERE p.isDeleted = false AND (" +
            "LOWER(p.policyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.policyHolder) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Policy> searchByNameOrHolder(@Param("searchTerm") String searchTerm);

    List<Policy> findByStatus(String status);

    @Query("SELECT p FROM Policy p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Policy> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    long countByStatus(String status);

    @Query("SELECT p FROM Policy p WHERE p.isDeleted = false")
    List<Policy> findAllActivePoliciesWithDetails();

    List<Policy> findByStatusNotAndExpiryDateBefore(String status, LocalDate expiryDate, Pageable pageable);

    List<Policy> findByExpiryDate(LocalDate expiryDate, Pageable pageable);

    List<Policy> findByStatusNotAndExpiryDateBefore(String status, LocalDate expiryDate);

    List<Policy> findByExpiryDate(LocalDate expiryDate);

    Page<Policy> findByIsDeletedFalse(Pageable pageable);

    Optional<Policy> findByIdAndIsDeletedFalse(Long id);

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(String status);
}