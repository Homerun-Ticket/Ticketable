package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.RedisConst;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProceedQueueService {
	private final StringRedisTemplate stringRedisTemplate;

	public boolean isContains(String token) {
		 return stringRedisTemplate.opsForZSet().rank(RedisConst.PROCEED_KEY, token) != null;
	}

	public void enterProceed(Set<TypedTuple<String>> waitingList) {
		stringRedisTemplate.opsForZSet().addIfAbsent(RedisConst.PROCEED_KEY, waitingList);
	}

	public long getSize() {
		Long size = stringRedisTemplate.opsForZSet().size(RedisConst.PROCEED_KEY);
		return size == null ? 0 : size;
	}

	public long getOrder(String token) {
		Long rank = stringRedisTemplate.opsForZSet().rank(RedisConst.PROCEED_KEY, token);
		return rank == null ? -1 : rank+1;
	}

	public void removeToken(String token) {
		stringRedisTemplate.opsForZSet().remove(RedisConst.PROCEED_KEY, token);
	}
}
