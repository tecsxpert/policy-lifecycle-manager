package com.internship.tool.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory per-IP rate limiting filter using Bucket4j.
 *
 * <ul>
 *   <li>Auth endpoints (/api/auth/**): 100 requests per minute per IP</li>
 *   <li>Policy endpoints (/api/policies/**): 200 requests per minute per IP</li>
 *   <li>All other endpoints: 300 requests per minute per IP</li>
 * </ul>
 *
 * Returns HTTP 429 Too Many Requests when the limit is exceeded.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> policyBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> defaultBuckets = new ConcurrentHashMap<>();

    private Bucket createBucket(long capacity) {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(capacity, Duration.ofMinutes(1)))
                .build();
    }

    private Bucket resolveBucket(String ip, String path) {
        if (path.startsWith("/api/auth/")) {
            return authBuckets.computeIfAbsent(ip, k -> createBucket(100));
        } else if (path.startsWith("/api/policies/")) {
            return policyBuckets.computeIfAbsent(ip, k -> createBucket(200));
        } else {
            return defaultBuckets.computeIfAbsent(ip, k -> createBucket(300));
        }
    }

    /**
     * Extracts the real client IP address, respecting the X-Forwarded-For header
     * when running behind a reverse proxy (e.g., Nginx in Docker).
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For can contain multiple IPs; use the first (original client)
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = extractClientIp(request);
        String path = request.getRequestURI();

        Bucket bucket = resolveBucket(clientIp, path);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("Retry-After", String.valueOf(waitForRefill));
            response.getWriter().write(
                    "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Retry after "
                            + waitForRefill + " seconds.\"}");
        }
    }
}

