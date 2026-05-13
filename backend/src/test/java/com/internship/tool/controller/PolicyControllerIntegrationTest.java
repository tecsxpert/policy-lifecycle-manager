package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.dto.PolicyRequestDTO;
import com.internship.tool.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Test
    void shouldCreatePolicyWhenAuthorized() throws Exception {
        doNothing().when(emailService).sendPolicyCreatedEmail("knowmore089@gmail.com", "Business Liability Plan");

        String token = mockMvc.perform(get("/auth/login").param("username", "admin"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(token).get("token").asText();

        PolicyRequestDTO request = new PolicyRequestDTO();
        request.setPolicyNumber("POL3001");
        request.setPolicyName("Business Liability Plan");
        request.setPolicyType("Business");
        request.setPremiumAmount(java.math.BigDecimal.valueOf(12500));
        request.setStartDate(java.time.LocalDate.of(2025, 2, 1));
        request.setEndDate(java.time.LocalDate.of(2026, 2, 1));
        request.setStatus("ACTIVE");

        mockMvc.perform(post("/policies")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber").value("POL3001"))
                .andExpect(jsonPath("$.policyName").value("Business Liability Plan"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
