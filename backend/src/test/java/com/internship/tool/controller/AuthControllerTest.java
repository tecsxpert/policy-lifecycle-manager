package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegister_Returns201AndCreatesUser() throws Exception {
        String json = """
            {"username": "newuser", "password": "pass123", "email": "new@example.com"}
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("VIEWER"));

        assertThat(userRepository.findByUsername("newuser")).isPresent();
    }

    @Test
    void testRegister_DuplicateUsername_Returns409() throws Exception {
        User existing = new User();
        existing.setUsername("existing");
        existing.setPassword(passwordEncoder.encode("pass"));
        existing.setEmail("ex@example.com");
        existing.setRole("VIEWER");
        userRepository.save(existing);

        String json = """
            {"username": "existing", "password": "pass123", "email": "new@example.com"}
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username already taken"));
    }

    @Test
    void testLogin_WithValidCredentials_ReturnsToken() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("secret"));
        user.setEmail("login@example.com");
        user.setRole("ADMIN");
        userRepository.save(user);

        String json = """
            {"username": "loginuser", "password": "secret"}
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLogin_WithInvalidCredentials_Returns401() throws Exception {
        User user = new User();
        user.setUsername("baduser");
        user.setPassword(passwordEncoder.encode("correct"));
        user.setEmail("bad@example.com");
        user.setRole("VIEWER");
        userRepository.save(user);

        String json = """
            {"username": "baduser", "password": "wrong"}
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void testRefresh_WithValidToken_ReturnsNewToken() throws Exception {
        User user = new User();
        user.setUsername("refreshuser");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setEmail("ref@example.com");
        user.setRole("MANAGER");
        userRepository.save(user);

        // First login to get a token
        String loginJson = """
            {"username": "refreshuser", "password": "pass"}
            """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        String refreshJson = String.format("{\"token\": \"%s\"}", token);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("refreshuser"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testRefresh_WithInvalidToken_Returns401() throws Exception {
        String json = """
            {"token": "invalid.token.here"}
            """;

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or expired token"));
    }
}

