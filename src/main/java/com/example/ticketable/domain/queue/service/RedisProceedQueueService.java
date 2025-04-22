package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.QueueSystemConstants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisProceedQueueService implements ProceedQueueService {
	private final ZSetOperations<String, String> proceedQueue;
	private final RedisTemplate<String, String> scriptRunner;
	private final DefaultRedisScript<Long> moveWaitingToProceedScript;

	public RedisProceedQueueService(RedisTemplate<String, String> redisTemplate, DefaultRedisScript<Long> moveWaitingToProceedScript) {
		this.moveWaitingToProceedScript = moveWaitingToProceedScript;
		this.proceedQueue = redisTemplate.opsForZSet();
		this.scriptRunner = redisTemplate;
	}

	//토큰이 작업열에 존재하는지 확인
	@Override
	public boolean isContains(String token) {
		 return proceedQueue.rank(QueueSystemConstants.PROCEED_QUEUE_KEY, token) != null;
	}

	//작업열에서 토큰 제거
	@Override
	public void removeToken(String token) {
		proceedQueue.remove(QueueSystemConstants.PROCEED_QUEUE_KEY, token);
	}

	//대기열에서 작업열로 targetSize가 될때까지 이동
	@Override
	public void pullFromWaitingQueue(Long targetSize) {
		scriptRunner.execute(moveWaitingToProceedScript,
			List.of(QueueSystemConstants.WAITING_QUEUE_KEY, QueueSystemConstants.PROCEED_QUEUE_KEY),
			String.valueOf(targetSize)
		);
	}

	@Override
	public void removeExpiredTokens() {
		long expire = System.currentTimeMillis() - QueueSystemConstants.TOKEN_EXPIRES;
		proceedQueue.removeRangeByScore(QueueSystemConstants.PROCEED_QUEUE_KEY,0,expire);
	}
}
