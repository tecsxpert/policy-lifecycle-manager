package com.internship.tool.controller;

import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;
import com.internship.tool.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
@Tag(name = "Policy APIs", description = "CRUD operations for policies")
@SecurityRequirement(name = "Bearer JWT")
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    @Operation(summary = "Create a new policy", description = "Creates a new policy with provided details")
    @ApiResponse(responseCode = "201", description = "Policy created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or duplicate policy number")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<PolicyResponseDTO> createPolicy(@Valid @RequestBody PolicyRequestDTO request) {
        PolicyResponseDTO response = policyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all policies", description = "Retrieves all policies from the system (cached)")
    @ApiResponse(responseCode = "200", description = "Policies fetched successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<List<PolicyResponseDTO>> getAllPolicies() {
        List<PolicyResponseDTO> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get policy by ID", description = "Retrieves a specific policy by its ID")
    @ApiResponse(responseCode = "200", description = "Policy fetched successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<PolicyResponseDTO> getPolicyById(@PathVariable Long id) {
        PolicyResponseDTO policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update policy", description = "Updates an existing policy with new details")
    @ApiResponse(responseCode = "200", description = "Policy updated successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<PolicyResponseDTO> updatePolicy(@PathVariable Long id,
            @Valid @RequestBody PolicyRequestDTO request) {
        PolicyResponseDTO updatedPolicy = policyService.updatePolicy(id, request);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete policy", description = "Deletes a policy by its ID")
    @ApiResponse(responseCode = "204", description = "Policy deleted successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}