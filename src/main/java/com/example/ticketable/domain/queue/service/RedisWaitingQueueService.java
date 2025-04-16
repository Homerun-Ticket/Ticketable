package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.QueueSystemConstants;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisWaitingQueueService implements WaitingQueueService {
	private final ZSetOperations<String, String> waitingQueue;

	//대기열 입장 후 토큰 반환
	@Override
	public String enterWaitingQueue() {
		String token = UUID.randomUUID().toString();
		long now = System.currentTimeMillis();
		waitingQueue.addIfAbsent(QueueSystemConstants.WAITING_QUEUE_KEY, token, now);
		return token;
	}

	//대기열 순서 조회
	@Override
	public long getOrder(String token) {
		Long rank = waitingQueue.rank(QueueSystemConstants.WAITING_QUEUE_KEY, token);
		return rank == null ? -1 : rank;
	}

	@Override
	public void removeToken(String token) {
		waitingQueue.remove(QueueSystemConstants.WAITING_QUEUE_KEY, token);
	}
}
