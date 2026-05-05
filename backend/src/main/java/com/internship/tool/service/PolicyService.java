package com.internship.tool.service;

import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;

import java.util.List;

public interface PolicyService {

    PolicyResponseDTO createPolicy(PolicyRequestDTO request);

    List<PolicyResponseDTO> getAllPolicies();

    PolicyResponseDTO getPolicyById(Long id);

    PolicyResponseDTO updatePolicy(Long id, PolicyRequestDTO request);

    void deletePolicy(Long id);
}
