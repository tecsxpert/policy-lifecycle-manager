package com.internship.tool.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * Redis Cache Configuration
 *
 * Configures Spring Boot to use Redis as the distributed cache provider
 * with the following settings:
 * - TTL: 10 minutes per cached entry
 * - Null values: Not cached to avoid storing false negatives
 *
 * This configuration is only active when spring.cache.type=redis
 * For testing or development with simple caching, this bean will not be loaded.
 *
 * The actual Redis connection is configured via environment variables:
 * - REDIS_HOST (default: localhost)
 * - REDIS_PORT (default: 6379)
 *
 * Cache behavior:
 * - @Cacheable: Stores method results in Redis for 10 minutes
 * - @CacheEvict: Removes entries from Redis when data is modified
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {

    /**
     * Configure RedisCacheManager with 10-minute TTL
     *
     * @param redisConnectionFactory The Redis connection factory
     * @return Configured RedisCacheManager instance
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // Configure default cache with 10-minute TTL
        // This will store cache entries in Redis and expire them after 10 minutes
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}
