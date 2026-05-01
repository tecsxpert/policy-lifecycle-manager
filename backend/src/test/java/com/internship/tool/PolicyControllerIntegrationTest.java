package com.internship.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.Policy;
import com.internship.tool.entity.User;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String viewerToken;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        userRepository.save(admin);

        User viewer = new User();
        viewer.setUsername("viewer");
        viewer.setPassword(passwordEncoder.encode("viewer123"));
        viewer.setEmail("viewer@test.com");
        viewer.setRole("VIEWER");
        userRepository.save(viewer);

        adminToken = jwtUtil.generateToken("admin");
        viewerToken = jwtUtil.generateToken("viewer");
    }

    @Test
    void testGetAllPolicies_Returns200AndPaginatedList() throws Exception {
        // Seed 3 policies
        for (int i = 1; i <= 3; i++) {
            Policy p = new Policy();
            p.setPolicyName("Policy " + i);
            p.setPolicyType("Auto Insurance");
            p.setStatus("Active");
            p.setPolicyHolder("Holder " + i);
            p.setExpiryDate(LocalDate.now().plusMonths(i));
            p.setIsDeleted(false);
            policyRepository.save(p);
        }

        mockMvc.perform(get("/api/policies/all")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void testCreatePolicy_Returns201Created() throws Exception {
        Policy policy = new Policy();
        policy.setPolicyName("New Test Policy");
        policy.setPolicyType("Home Insurance");
        policy.setStatus("Active");
        policy.setPolicyHolder("Test Holder");
        policy.setExpiryDate(LocalDate.now().plusYears(1));

        mockMvc.perform(post("/api/policies/create")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.policyName").value("New Test Policy"))
                .andExpect(jsonPath("$.isDeleted").value(false));
    }

    @Test
    void testSoftDeletePolicy_Returns200AndUpdatesIsDeletedFlag() throws Exception {
        Policy policy = new Policy();
        policy.setPolicyName("To Be Deleted");
        policy.setPolicyType("Travel Insurance");
        policy.setStatus("Active");
        policy.setPolicyHolder("Delete Me");
        policy.setExpiryDate(LocalDate.now().plusMonths(6));
        policy.setIsDeleted(false);
        Policy saved = policyRepository.save(policy);

        mockMvc.perform(delete("/api/policies/{id}", saved.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        Policy deleted = policyRepository.findById(saved.getId()).orElseThrow();
        assertTrue(deleted.getIsDeleted(), "is_deleted flag should be TRUE after soft delete");
        assertEquals("DELETED", deleted.getStatus(), "Status should be DELETED after soft delete");
    }

    @Test
    void testDeletePolicy_AsViewer_Returns403Forbidden() throws Exception {
        Policy policy = new Policy();
        policy.setPolicyName("Viewer Delete Attempt");
        policy.setPolicyType("Life Insurance");
        policy.setStatus("Active");
        policy.setPolicyHolder("Viewer Holder");
        policy.setExpiryDate(LocalDate.now().plusMonths(3));
        policy.setIsDeleted(false);
        Policy saved = policyRepository.save(policy);

        mockMvc.perform(delete("/api/policies/{id}", saved.getId())
                        .header("Authorization", "Bearer " + viewerToken))
                .andExpect(status().isForbidden());
    }
}

