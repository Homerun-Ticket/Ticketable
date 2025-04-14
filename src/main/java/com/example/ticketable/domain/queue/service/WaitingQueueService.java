package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.RedisConst;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {
	private final StringRedisTemplate stringRedisTemplate;

	//대기열 입장 후 토큰 반환
	public String enterWaitingQueue() {
		String token = UUID.randomUUID().toString();
		long now = System.currentTimeMillis();
		stringRedisTemplate.opsForZSet().addIfAbsent(RedisConst.WAITING_KEY, token, now);
		return token;
	}

	//대기열 순서 조회
	public long getOrder(String token) {
		Long rank = stringRedisTemplate.opsForZSet().rank(RedisConst.WAITING_KEY, token);
		return rank == null ? -1 : rank+1;
	}

	//count 만큼 대기열에서 pop
	public Set<TypedTuple<String>> popMin(long count) {
		return stringRedisTemplate.opsForZSet().popMin(RedisConst.WAITING_KEY, count);
	}
}
