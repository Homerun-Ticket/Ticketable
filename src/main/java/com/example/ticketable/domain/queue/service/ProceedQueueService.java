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

	//토큰이 작업열에 존재하는지 확인
	public boolean isContains(String token) {
		 return stringRedisTemplate.opsForZSet().rank(RedisConst.PROCEED_KEY, token) != null;
	}

	//작업열에 추가
	public void enterProceed(Set<TypedTuple<String>> waitingList) {
		stringRedisTemplate.opsForZSet().addIfAbsent(RedisConst.PROCEED_KEY, waitingList);
	}

	//작업열 사이즈 반환
	public long getSize() {
		Long size = stringRedisTemplate.opsForZSet().size(RedisConst.PROCEED_KEY);
		return size == null ? 0 : size;
	}

	//작업열에서 토큰 제거
	public void removeToken(String token) {
		stringRedisTemplate.opsForZSet().remove(RedisConst.PROCEED_KEY, token);
	}
}
