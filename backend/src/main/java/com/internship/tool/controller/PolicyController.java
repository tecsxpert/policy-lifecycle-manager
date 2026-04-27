package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.util.InputSanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Endpoints for managing insurance policies")
@SecurityRequirement(name = "bearerAuth")
public class PolicyController {

    @Autowired
    private PolicyRepository policyRepository;

    @Operation(
            summary = "Get all policies",
            description = "Returns a paginated list of all non-deleted policies. Accessible by ADMIN, MANAGER, and VIEWER roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated policies",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden — insufficient role privileges")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPolicies(Pageable pageable) {
        Page<Policy> page = policyRepository.findByIsDeletedFalse(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("pageSize", page.getSize());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new policy",
            description = "Creates a new insurance policy. Restricted to ADMIN and MANAGER roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Policy created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Policy.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request — invalid policy data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER role")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Policy object to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Policy.class))
            )
            @RequestBody Policy policy) {
        policy.setIsDeleted(false);
        policy.setPolicyName(InputSanitizer.sanitize(policy.getPolicyName()));
        policy.setPolicyType(InputSanitizer.sanitize(policy.getPolicyType()));
        policy.setStatus(InputSanitizer.sanitize(policy.getStatus()));
        policy.setPolicyHolder(InputSanitizer.sanitize(policy.getPolicyHolder()));
        Policy saved = policyRepository.save(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
            summary = "Update an existing policy",
            description = "Updates a policy by its ID. Restricted to ADMIN and MANAGER roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Policy.class))),
            @ApiResponse(responseCode = "404", description = "Policy not found or already deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER role")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(
            @Parameter(description = "ID of the policy to update", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated policy details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Policy.class))
            )
            @RequestBody Policy policyDetails) {
        return policyRepository.findByIdAndIsDeletedFalse(id)
                .map(policy -> {
                    policy.setPolicyName(InputSanitizer.sanitize(policyDetails.getPolicyName()));
                    policy.setPolicyType(InputSanitizer.sanitize(policyDetails.getPolicyType()));
                    policy.setStatus(InputSanitizer.sanitize(policyDetails.getStatus()));
                    policy.setPolicyHolder(InputSanitizer.sanitize(policyDetails.getPolicyHolder()));
                    policy.setExpiryDate(policyDetails.getExpiryDate());
                    Policy updatedPolicy = policyRepository.save(policy);
                    return ResponseEntity.ok(updatedPolicy);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Soft delete a policy",
            description = "Performs a soft delete by setting the is_deleted flag to TRUE and status to DELETED. Restricted to ADMIN role only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found or already deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePolicy(
            @Parameter(description = "ID of the policy to soft delete", example = "1")
            @PathVariable Long id) {
        return policyRepository.findByIdAndIsDeletedFalse(id)
                .map(policy -> {
                    policy.setIsDeleted(true);
                    policy.setStatus("DELETED");
                    policyRepository.save(policy);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search policies",
            description = "Searches policies by name or policy holder using a case-insensitive partial match."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Policy.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<List<Policy>> searchPolicies(
            @Parameter(description = "Search term for policy name or holder", example = "John")
            @RequestParam(name = "q") String q) {
        String sanitizedQuery = InputSanitizer.sanitize(q);
        List<Policy> results = policyRepository.searchByNameOrHolder(sanitizedQuery);
        return ResponseEntity.ok(results);
    }

    @Operation(
            summary = "Get policy statistics",
            description = "Returns KPI stats: total non-deleted policies and active policy count."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PolicyStatsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPolicyStats() {
        long totalPolicies = policyRepository.countByIsDeletedFalse();
        long totalActive = policyRepository.countByStatusAndIsDeletedFalse("Active");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPolicies", totalPolicies);
        stats.put("totalActivePolicies", totalActive);

        return ResponseEntity.ok(stats);
    }

    @Schema(name = "PolicyPageResponse", description = "Paginated response wrapper for policies")
    public static class PolicyPageResponse {
        @Schema(description = "List of policy objects", example = "[{\"id\":1,\"policyName\":\"AutoShield\"}]")
        public List<Policy> content;
        @Schema(description = "Total number of elements", example = "30")
        public Long totalElements;
        @Schema(description = "Total number of pages", example = "3")
        public Integer totalPages;
        @Schema(description = "Current page number (0-indexed)", example = "0")
        public Integer currentPage;
        @Schema(description = "Page size", example = "10")
        public Integer pageSize;
    }

    @Schema(name = "PolicyStatsResponse", description = "Policy KPI statistics")
    public static class PolicyStatsResponse {
        @Schema(description = "Total non-deleted policies", example = "30")
        public Long totalPolicies;
        @Schema(description = "Total active policies", example = "25")
        public Long totalActivePolicies;
    }
}
