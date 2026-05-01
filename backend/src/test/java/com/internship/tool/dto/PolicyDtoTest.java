package com.internship.tool.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyDtoTest {

    @Test
    void testPolicyRequestGettersSetters() {
        PolicyRequest req = new PolicyRequest();
        req.setPolicyName("Test");
        req.setPolicyType("Auto");
        req.setStatus("Active");
        req.setPolicyHolder("John");
        req.setExpiryDate(LocalDate.of(2026, 1, 1));

        assertThat(req.getPolicyName()).isEqualTo("Test");
        assertThat(req.getPolicyType()).isEqualTo("Auto");
        assertThat(req.getStatus()).isEqualTo("Active");
        assertThat(req.getPolicyHolder()).isEqualTo("John");
        assertThat(req.getExpiryDate()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void testPolicyResponseGettersSetters() {
        PolicyResponse resp = new PolicyResponse();
        resp.setId(1L);
        resp.setPolicyName("Test");
        resp.setPolicyType("Auto");
        resp.setStatus("Active");
        resp.setPolicyHolder("John");
        resp.setExpiryDate(LocalDate.now());
        resp.setIsDeleted(false);
        resp.setCreatedAt(LocalDateTime.now());
        resp.setUpdatedAt(LocalDateTime.now());

        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getPolicyName()).isEqualTo("Test");
        assertThat(resp.getPolicyType()).isEqualTo("Auto");
        assertThat(resp.getStatus()).isEqualTo("Active");
        assertThat(resp.getPolicyHolder()).isEqualTo("John");
    }
}
