package com.internship.tool.security;

import com.internship.tool.config.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void shouldGenerateToken() {
        String token = jwtUtil.generateToken("admin");

        assertNotNull(token, "Generated JWT token should not be null");
        assertTrue(token.length() > 0, "Generated JWT token should not be empty");
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtUtil.generateToken("admin");
        String username = jwtUtil.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken("admin");

        assertTrue(jwtUtil.validateToken(token), "Valid token should be accepted");
    }

    @Test
    void shouldInvalidateWrongToken() {
        String token = jwtUtil.generateToken("admin") + "broken";

        assertFalse(jwtUtil.validateToken(token), "Invalid token should be rejected");
    }
}
