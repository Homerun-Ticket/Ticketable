package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.RedisConst;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProceedQueueService {
	private final StringRedisTemplate stringRedisTemplate;
	private final DefaultRedisScript<Long> moveWaitingToProceedScript;

	//토큰이 작업열에 존재하는지 확인
	public boolean isContains(String token) {
		 return stringRedisTemplate.opsForZSet().rank(RedisConst.PROCEED_KEY, token) != null;
	}

	//작업열에서 토큰 제거
	public void removeToken(String token) {
		stringRedisTemplate.opsForZSet().remove(RedisConst.PROCEED_KEY, token);
	}

	//대기열에서 작업열로 targetSize가 될때까지 이동
	public void pullFromWaitingQueue(Long targetSize) {
		stringRedisTemplate.execute(moveWaitingToProceedScript,
			List.of(RedisConst.WAITING_KEY, RedisConst.PROCEED_KEY),
			String.valueOf(targetSize)
		);
	}
}
