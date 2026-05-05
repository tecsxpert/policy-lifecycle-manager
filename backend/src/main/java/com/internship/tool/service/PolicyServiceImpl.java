package com.internship.tool.service;

import com.internship.tool.dto.PolicyMapper;
import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;
import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;
import com.internship.tool.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.internship.tool.exception.InvalidRequestException;
import com.internship.tool.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    @CacheEvict(value = "policies", allEntries = true)
    @Override
    public PolicyResponseDTO createPolicy(PolicyRequestDTO request) {
        Policy saved = policyRepository.save(PolicyMapper.toEntity(request));
        return PolicyMapper.toResponse(saved);
    }

    @Cacheable("policies")
    @Override
    public List<PolicyResponseDTO> getAllPolicies() {
        System.out.println("Fetching from DB...");

        return policyRepository.findAll().stream()
                .map(PolicyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "policy", key = "#id")
    @Override
    public PolicyResponseDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return PolicyMapper.toResponse(policy);
    }

    @CacheEvict(value = { "policies", "policy" }, allEntries = true)
    @Override
    public PolicyResponseDTO updatePolicy(Long id, PolicyRequestDTO request) {
        Policy existingPolicy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));

        existingPolicy.setPolicyNumber(request.getPolicyNumber());
        existingPolicy.setPolicyName(request.getPolicyName());
        existingPolicy.setPolicyType(request.getPolicyType());
        existingPolicy.setPremiumAmount(request.getPremiumAmount());
        existingPolicy.setStartDate(request.getStartDate());
        existingPolicy.setEndDate(request.getEndDate());
        existingPolicy.setStatus(resolvePolicyStatus(request.getStatus()));

        Policy updatedPolicy = policyRepository.save(existingPolicy);
        return PolicyMapper.toResponse(updatedPolicy);
    }

    @CacheEvict(value = { "policies", "policy" }, allEntries = true)
    @Override
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    private PolicyStatus resolvePolicyStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidRequestException("Policy status must be provided");
        }

        try {
            return PolicyStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException(
                    String.format("Invalid policy status '%s'. Allowed values: %s", status,
                            String.join(", ",
                                    Arrays.stream(PolicyStatus.values())
                                            .map(Enum::name)
                                            .toList())));
        }
    }
}
