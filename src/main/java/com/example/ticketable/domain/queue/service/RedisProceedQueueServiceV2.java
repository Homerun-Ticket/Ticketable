package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.QueueSystemConstants;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisProceedQueueServiceV2 implements ProceedQueueService {

	private final ValueOperations<String, String> proceedList;
	private final RedisTemplate<String, String> scriptRunner;
	private final DefaultRedisScript<Long> moveWaitingToProceedV2;

	public RedisProceedQueueServiceV2(RedisTemplate<String, String> redisTemplate, DefaultRedisScript<Long> moveWaitingToProceedScriptV2) {
		this.proceedList = redisTemplate.opsForValue();
		this.scriptRunner = redisTemplate;
		this.moveWaitingToProceedV2 = moveWaitingToProceedScriptV2;
	}

	@Override
	public boolean isContains(String token) {
		return proceedList.get(QueueSystemConstants.PROCEED_QUEUE_KEY+":"+ token) != null;
	}

	@Override
	public void removeToken(String token) {
		proceedList.getOperations().delete(QueueSystemConstants.PROCEED_QUEUE_KEY+":"+ token);
	}

	@Override
	public void pullFromWaitingQueue(Long targetSize) {
		scriptRunner.execute(moveWaitingToProceedV2,
			List.of(QueueSystemConstants.WAITING_QUEUE_KEY, QueueSystemConstants.PROCEED_QUEUE_KEY),
			String.valueOf(targetSize));
	}

	@Override
	public void removeExpiredTokens() {
		return;
	}
}
