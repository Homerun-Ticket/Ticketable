package com.example.ticketable.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("seatCountsBySectionType");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES) // 30분 후 자동 삭제
        );

        return cacheManager;
    }
// 추후 분산 서버로 인한 레디스캐싱으로 변경 시 사용 예정
//@Bean
//public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//    // Jackson2JsonRedisSerializer로 JSON 직렬화 설정
//    ObjectMapper objectMapper = new ObjectMapper();
//    objectMapper.registerModule(new JavaTimeModule());
//    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//    RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//            .entryTtl(Duration.ofMinutes(30))
//            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
//
//
//    return RedisCacheManager.builder(redisConnectionFactory)
//            .cacheDefaults(config)
//            .build();
//    }
}
