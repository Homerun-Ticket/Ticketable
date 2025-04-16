package com.example.ticketable.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@Configuration
public class RedisConfig {

	@Bean
	public ZSetOperations<String, String> zSetOperations(RedisTemplate<String, String> redisTemplate) {
		return redisTemplate.opsForZSet();
	}
}
