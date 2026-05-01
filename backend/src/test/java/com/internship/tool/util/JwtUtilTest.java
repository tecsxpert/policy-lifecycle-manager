package com.internship.tool.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "dGVzdHNlY3JldGtleWZvcnRlc3Rpbmdvbmx5dGVzdHNlY3JldA==");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L);
    }

    @Test
    void testGenerateToken_ReturnsNonNullString() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void testValidateToken_WithValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("validuser");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void testValidateToken_WithInvalidToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void testValidateToken_WithMalformedToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void testGetUsernameFromToken_ReturnsCorrectUsername() {
        String expectedUsername = "john_doe";
        String token = jwtUtil.generateToken(expectedUsername);
        String actualUsername = jwtUtil.getUsernameFromToken(token);
        assertThat(actualUsername).isEqualTo(expectedUsername);
    }

    @Test
    void testGenerateToken_ProducesDifferentTokensForDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");
        assertThat(token1).isNotEqualTo(token2);
    }
}

