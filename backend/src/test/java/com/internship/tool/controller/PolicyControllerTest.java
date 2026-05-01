package com.internship.tool.controller;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.service.EmailService;
import com.internship.tool.service.NoOpEmailService;
import com.internship.tool.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PolicyControllerTest {

    private MockMvc mockMvc;
    private PolicyRepository policyRepository;
    private PolicyService policyService;
    private PolicyController policyController;

    @BeforeEach
    void setUp() {
        policyRepository = mock(PolicyRepository.class);
        EmailService emailService = new NoOpEmailService();

        policyService = new PolicyService();
        ReflectionTestUtils.setField(policyService, "policyRepository", policyRepository);
        ReflectionTestUtils.setField(policyService, "emailService", emailService);

        policyController = new PolicyController();
        ReflectionTestUtils.setField(policyController, "policyService", policyService);

        mockMvc = MockMvcBuilders.standaloneSetup(policyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPolicies() throws Exception {
        Policy p = createPolicyEntity(1L, "Test Policy");
        when(policyRepository.findByIsDeletedFalse(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(p)));

        mockMvc.perform(get("/api/policies/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePolicy() throws Exception {
        Policy saved = createPolicyEntity(1L, "New Policy");
        when(policyRepository.save(any(Policy.class))).thenReturn(saved);

        mockMvc.perform(post("/api/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "policyName": "New Policy",
                                "policyType": "Auto",
                                "status": "Active",
                                "policyHolder": "John",
                                "expiryDate": "2026-12-31"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyName").value("New Policy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePolicy() throws Exception {
        Policy existing = createPolicyEntity(1L, "Old Policy");
        Policy updated = createPolicyEntity(1L, "Updated Policy");
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(policyRepository.save(any(Policy.class))).thenReturn(updated);

        mockMvc.perform(put("/api/policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "policyName": "Updated Policy",
                                "policyType": "Auto",
                                "status": "Active",
                                "policyHolder": "John",
                                "expiryDate": "2026-12-31"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Updated Policy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSoftDeletePolicy() throws Exception {
        Policy existing = createPolicyEntity(1L, "To Delete");
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(policyRepository.save(any(Policy.class))).thenReturn(existing);

        mockMvc.perform(delete("/api/policies/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPolicies() throws Exception {
        when(policyRepository.searchByNameOrHolder(any())).thenReturn(List.of(createPolicyEntity(1L, "Test")));

        mockMvc.perform(get("/api/policies/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPolicyStats() throws Exception {
        when(policyRepository.countByIsDeletedFalse()).thenReturn(10L);
        when(policyRepository.countByStatusAndIsDeletedFalse("Active")).thenReturn(5L);

        mockMvc.perform(get("/api/policies/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPolicies").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testExportPoliciesToCsv() throws Exception {
        Policy p = createPolicyEntity(1L, "Test Policy");
        p.setPolicyType("Auto Insurance");
        p.setStatus("Active");
        p.setPolicyHolder("John Doe");
        p.setExpiryDate(LocalDate.of(2026, 1, 1));
        p.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        p.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        when(policyRepository.findByIsDeletedFalse(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(p)));

        mockMvc.perform(get("/api/policies/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/csv")));
    }

    private Policy createPolicyEntity(Long id, String name) {
        Policy p = new Policy();
        p.setId(id);
        p.setPolicyName(name);
        p.setPolicyType("Auto");
        p.setStatus("Active");
        p.setPolicyHolder("John");
        p.setExpiryDate(LocalDate.now().plusYears(1));
        p.setIsDeleted(false);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return p;
    }
}
