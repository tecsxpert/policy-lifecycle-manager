package com.internship.tool.controller;

import com.internship.tool.dto.PolicyRequest;
import com.internship.tool.dto.PolicyResponse;
import com.internship.tool.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Endpoints for managing insurance policies")
@SecurityRequirement(name = "bearerAuth")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @Operation(summary = "Get all policies", description = "Returns a paginated list of all non-deleted policies.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated policies"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPolicies(Pageable pageable) {
        Page<PolicyResponse> page = policyService.getAllPolicies(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("pageSize", page.getSize());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new policy", description = "Creates a new insurance policy. ADMIN/MANAGER only.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Policy created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request) {
        PolicyResponse created = policyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update an existing policy", description = "Updates a policy by ID. ADMIN/MANAGER only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policy updated successfully"),
        @ApiResponse(responseCode = "404", description = "Policy not found or already deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponse> updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        PolicyResponse updated = policyService.updatePolicy(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Soft delete a policy", description = "Sets is_deleted=true and status=DELETED. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policy soft-deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Policy not found or already deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePolicy(@PathVariable Long id) {
        policyService.softDeletePolicy(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search policies", description = "Searches by policy name or holder.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/search")
    public ResponseEntity<List<PolicyResponse>> searchPolicies(@RequestParam(name = "q") String q) {
        return ResponseEntity.ok(policyService.searchPolicies(q));
    }

    @Operation(summary = "Get policy statistics", description = "Returns total and active policy counts.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPolicyStats() {
        PolicyService.PolicyStats stats = policyService.getPolicyStats();
        Map<String, Object> response = new HashMap<>();
        response.put("totalPolicies", stats.getTotalPolicies());
        response.put("totalActivePolicies", stats.getTotalActivePolicies());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Export policies to CSV", description = "Exports all non-deleted policies as a CSV file.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file exported successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPoliciesToCsv() {
        List<PolicyResponse> policies = policyService.getAllActivePoliciesForExport();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Policy Name,Policy Type,Status,Policy Holder,Expiry Date,Created At,Updated At\n");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (PolicyResponse p : policies) {
            csv.append(p.getId()).append(",")
               .append(escapeCsv(p.getPolicyName())).append(",")
               .append(escapeCsv(p.getPolicyType())).append(",")
               .append(escapeCsv(p.getStatus())).append(",")
               .append(escapeCsv(p.getPolicyHolder())).append(",")
               .append(p.getExpiryDate() != null ? p.getExpiryDate() : "").append(",")
               .append(p.getCreatedAt() != null ? p.getCreatedAt().format(dtf) : "").append(",")
               .append(p.getUpdatedAt() != null ? p.getUpdatedAt().format(dtf) : "").append("\n");
        }
        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "policies_export.csv");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

