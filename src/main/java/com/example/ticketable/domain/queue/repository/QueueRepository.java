package com.example.ticketable.domain.queue.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueRepository {
	private final RedisTemplate<String, String> redisTemplate;

	//대기열 추가
	public void addQueue(String key, String value) {
		long now = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(key, value , now);
	}

	//대기열 중복여부 검사
	public boolean isContains(String key, String value) {
		Double score = redisTemplate.opsForZSet().score(key, value);
		return score != null;
	}

	//대기열 순번 조회
	public Long getRank(String key, String value) {
		return redisTemplate.opsForZSet().rank(key, value);
	}

	public void delete(String key, String value) {
		redisTemplate.opsForZSet().remove(key, value);
	}

}
