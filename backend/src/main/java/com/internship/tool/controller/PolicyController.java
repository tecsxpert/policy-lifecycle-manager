package com.internship.tool.controller;

import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;
import com.internship.tool.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponseDTO> createPolicy(@Valid @RequestBody PolicyRequestDTO request) {
        PolicyResponseDTO response = policyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponseDTO>> getAllPolicies() {
        List<PolicyResponseDTO> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getPolicyById(@PathVariable Long id) {
        PolicyResponseDTO policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> updatePolicy(@PathVariable Long id,
            @Valid @RequestBody PolicyRequestDTO request) {
        PolicyResponseDTO updatedPolicy = policyService.updatePolicy(id, request);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}