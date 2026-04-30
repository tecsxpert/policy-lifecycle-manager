package com.internship.tool.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RateLimitingFilter to achieve 100% coverage.
 */
class RateLimitingFilterTest {

    @Mock
    private FilterChain filterChain;

    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimitingFilter = new RateLimitingFilter();
    }

    @Test
    void testCreateBucket_WithCapacity100() {
        // Test the createBucket method with different capacities
        Bucket bucket = invokeCreateBucket(100);
        assertNotNull(bucket);
    }

    @Test
    void testCreateBucket_WithCapacity200() {
        Bucket bucket = invokeCreateBucket(200);
        assertNotNull(bucket);
    }

    @Test
    void testCreateBucket_WithCapacity300() {
        Bucket bucket = invokeCreateBucket(300);
        assertNotNull(bucket);
    }

    @Test
    void testResolveBucket_AuthEndpoint() {
        // Test path starting with /api/auth/
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        request.setRemoteAddr("192.168.1.1");

        // Use reflection to call the private method
        Bucket bucket = invokeResolveBucket("192.168.1.1", "/api/auth/login");
        assertNotNull(bucket);
    }

    @Test
    void testResolveBucket_PolicyEndpoint() {
        // Test path starting with /api/policies/
        Bucket bucket = invokeResolveBucket("192.168.1.1", "/api/policies/all");
        assertNotNull(bucket);
    }

    @Test
    void testResolveBucket_DefaultEndpoint() {
        // Test other endpoints
        Bucket bucket = invokeResolveBucket("192.168.1.1", "/api/other");
        assertNotNull(bucket);
    }

    @Test
    void testExtractClientIp_WithXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "10.0.0.1, 192.168.1.1");
        request.setRemoteAddr("192.168.1.1");

        String ip = invokeExtractClientIp(request);
        assertEquals("10.0.0.1", ip);
    }

    @Test
    void testExtractClientIp_WithoutXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");

        String ip = invokeExtractClientIp(request);
        assertEquals("192.168.1.100", ip);
    }

    @Test
    void testExtractClientIp_WithEmptyXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "   ");
        request.setRemoteAddr("192.168.1.1");

        String ip = invokeExtractClientIp(request);
        assertEquals("192.168.1.1", ip);
    }

    @Test
    void testDoFilterInternal_RateLimitNotExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        request.setRemoteAddr("192.168.1.1");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Set up the filter chain to accept requests (empty the bucket)
        // By using a fresh IP, we should get through
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Verify filterChain was called
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Check rate limit remaining header is set
        assertNotNull(response.getHeader("X-Rate-Limit-Remaining"));
    }

    // Helper methods to access private methods via reflection
    private Bucket invokeCreateBucket(long capacity) {
        try {
            var method = RateLimitingFilter.class.getDeclaredMethod("createBucket", long.class);
            method.setAccessible(true);
            return (Bucket) method.invoke(rateLimitingFilter, capacity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Bucket invokeResolveBucket(String ip, String path) {
        try {
            var method = RateLimitingFilter.class.getDeclaredMethod("resolveBucket", String.class, String.class);
            method.setAccessible(true);
            return (Bucket) method.invoke(rateLimitingFilter, ip, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeExtractClientIp(HttpServletRequest request) {
        try {
            var method = RateLimitingFilter.class.getDeclaredMethod("extractClientIp", HttpServletRequest.class);
            method.setAccessible(true);
            return (String) method.invoke(rateLimitingFilter, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
