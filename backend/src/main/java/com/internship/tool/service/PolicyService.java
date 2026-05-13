package com.internship.tool.service;

import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.dto.PolicyResponseDTO;
import org.springframework.data.domain.Page;

public interface PolicyService {

    PolicyResponseDTO createPolicy(PolicyRequestDTO request);

    Page<PolicyResponseDTO> getAllPolicies(int page, int size);

    PolicyResponseDTO getPolicyById(Long id);

    PolicyResponseDTO updatePolicy(Long id, PolicyRequestDTO request);

    void deletePolicy(Long id);
}
