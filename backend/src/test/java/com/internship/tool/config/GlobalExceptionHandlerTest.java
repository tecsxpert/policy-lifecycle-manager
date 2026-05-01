package com.internship.tool.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestExceptionController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testHandleAccessDenied_Returns403() throws Exception {
        mockMvc.perform(get("/test/throw-access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void testHandleIllegalArgument_Returns400() throws Exception {
        mockMvc.perform(get("/test/throw-illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testHandleGenericException_Returns500() throws Exception {
        mockMvc.perform(get("/test/throw-generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestExceptionController {

        @GetMapping("/throw-access-denied")
        public String throwAccessDenied() {
            throw new AccessDeniedException("Access is denied");
        }

        @GetMapping("/throw-illegal-argument")
        public String throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/throw-generic")
        public String throwGeneric() {
            throw new RuntimeException("Something went wrong");
        }
    }
}
