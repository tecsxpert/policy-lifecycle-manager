package com.internship.tool.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuditLogEntityTest {

    @Test
    void testAuditLogFields() {
        AuditLog log = new AuditLog();
        LocalDateTime now = LocalDateTime.now();

        log.setId(1L);
        log.setEntityName("Policy");
        log.setEntityId(5L);
        log.setAction("CREATED");
        log.setChangedBy("admin");
        log.setChangeDate(now);
        log.setOldValue("{\"name\":\"old\"}");
        log.setNewValue("{\"name\":\"new\"}");

        assertThat(log.getId()).isEqualTo(1L);
        assertThat(log.getEntityName()).isEqualTo("Policy");
        assertThat(log.getEntityId()).isEqualTo(5L);
        assertThat(log.getAction()).isEqualTo("CREATED");
        assertThat(log.getChangedBy()).isEqualTo("admin");
        assertThat(log.getChangeDate()).isEqualTo(now);
        assertThat(log.getOldValue()).contains("old");
        assertThat(log.getNewValue()).contains("new");
    }
}
