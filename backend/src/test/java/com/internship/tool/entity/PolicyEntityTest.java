package com.internship.tool.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyEntityTest {

    @Test
    void testPolicyConstructorAndGettersSetters() {
        Policy policy = new Policy();
        LocalDate expiry = LocalDate.of(2026, 12, 31);
        LocalDateTime now = LocalDateTime.now();

        policy.setId(1L);
        policy.setPolicyName("Test Policy");
        policy.setPolicyType("Auto");
        policy.setStatus("Active");
        policy.setPolicyHolder("John");
        policy.setExpiryDate(expiry);
        policy.setIsDeleted(false);
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);

        assertThat(policy.getId()).isEqualTo(1L);
        assertThat(policy.getPolicyName()).isEqualTo("Test Policy");
        assertThat(policy.getPolicyType()).isEqualTo("Auto");
        assertThat(policy.getStatus()).isEqualTo("Active");
        assertThat(policy.getPolicyHolder()).isEqualTo("John");
        assertThat(policy.getExpiryDate()).isEqualTo(expiry);
        assertThat(policy.getIsDeleted()).isFalse();
        assertThat(policy.getCreatedAt()).isEqualTo(now);
        assertThat(policy.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testPolicyFullConstructor() {
        LocalDate expiry = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        Policy policy = new Policy(1L, "Name", "Type", "Active", "Holder", expiry, false, now, now);

        assertThat(policy.getPolicyName()).isEqualTo("Name");
    }

    @Test
    void testPolicyEqualsAndHashCode() {
        Policy p1 = new Policy();
        p1.setId(1L);
        Policy p2 = new Policy();
        p2.setId(1L);
        Policy p3 = new Policy();
        p3.setId(2L);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo("not a policy");
    }

    @Test
    void testPolicyToString() {
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setPolicyName("Test");

        assertThat(policy.toString()).contains("Policy").contains("id=1").contains("policyName='Test'");
    }
}
