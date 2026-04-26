package com.internship.tool;

import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.Policy;
import com.internship.tool.entity.User;
import com.internship.tool.repository.AuditRepository;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FullSystemIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("policydb")
            .withUsername("policyuser")
            .withPassword("policypass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        policyRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        userRepository.save(admin);

        adminToken = jwtUtil.generateToken("admin");
    }

    @Test
    void testFullPolicyLifecycle_WithAuditLog() throws Exception {
        // Step 1: Create a policy
        String createJson = """
            {
                "policyName": "FullCycle Policy",
                "policyType": "Life Insurance",
                "status": "Active",
                "policyHolder": "Lifecycle Holder",
                "expiryDate": "2026-12-31"
            }
            """;

        String createResponse = mockMvc.perform(post("/api/policies/create")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyName").value("FullCycle Policy"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long policyId = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .addModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .build()
                .readTree(createResponse)
                .get("id")
                .asLong();

        // Step 2: Retrieve the policy via GET /all
        mockMvc.perform(get("/api/policies/all")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].policyName").value("FullCycle Policy"));

        // Step 3: Update the policy
        String updateJson = """
            {
                "policyName": "FullCycle Policy Updated",
                "policyType": "Life Insurance",
                "status": "Pending",
                "policyHolder": "Lifecycle Holder Updated",
                "expiryDate": "2027-06-30"
            }
            """;

        mockMvc.perform(put("/api/policies/{id}", policyId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("FullCycle Policy Updated"));

        // Step 4: Soft delete the policy
        mockMvc.perform(delete("/api/policies/{id}", policyId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Step 5: Verify soft delete in DB
        Policy deletedPolicy = policyRepository.findById(policyId).orElseThrow();
        assertThat(deletedPolicy.getIsDeleted()).isTrue();
        assertThat(deletedPolicy.getStatus()).isEqualTo("DELETED");

        // Step 6: Verify audit logs were created
        List<AuditLog> auditLogs = auditRepository.findAll();
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .contains("POLICY_CREATED", "POLICY_UPDATED", "POLICY_DELETED");

        assertThat(auditLogs)
                .allMatch(log -> log.getEntityName().equals("Policy"));

        assertThat(auditLogs)
                .allMatch(log -> log.getChangedBy().equals("admin"));
    }

    @Test
    void testFlywayMigrationsRunSuccessfully() {
        // If the context loads and migrations run, this test passes
        long policyCount = policyRepository.count();
        assertThat(policyCount).isGreaterThanOrEqualTo(0);
    }
}

