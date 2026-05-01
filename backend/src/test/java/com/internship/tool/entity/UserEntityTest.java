package com.internship.tool.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void testUserConstructorAndGettersSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpass");
        user.setEmail("test@example.com");
        user.setRole("ADMIN");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("hashedpass");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testUserFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "admin", "pass", "admin@test.com", "ADMIN", now);

        assertThat(user.getUsername()).isEqualTo("admin");
    }

    @Test
    void testUserEqualsAndHashCode() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(1L);
        User u3 = new User();
        u3.setId(2L);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1).isNotEqualTo(u3);
    }

    @Test
    void testUserToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");

        assertThat(user.toString()).contains("User").contains("id=1").contains("username='admin'");
    }
}
