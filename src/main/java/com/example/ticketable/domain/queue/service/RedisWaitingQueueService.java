package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.QueueSystemConstants;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisWaitingQueueService implements WaitingQueueService {
	private final StringRedisTemplate stringRedisTemplate;

	//대기열 입장 후 토큰 반환
	public String enterWaitingQueue() {
		String token = UUID.randomUUID().toString();
		long now = System.currentTimeMillis();
		stringRedisTemplate.opsForZSet().addIfAbsent(QueueSystemConstants.WAITING_QUEUE_KEY, token, now);
		return token;
	}

	//대기열 순서 조회
	public long getOrder(String token) {
		Long rank = stringRedisTemplate.opsForZSet().rank(QueueSystemConstants.WAITING_QUEUE_KEY, token);
		return rank == null ? -1 : rank+1;
	}

	public void removeToken(String token) {
		stringRedisTemplate.opsForZSet().remove(QueueSystemConstants.WAITING_QUEUE_KEY, token);
	}
}
