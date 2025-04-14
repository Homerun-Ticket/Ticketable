package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.RedisConst;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueManager {
	private final WaitingQueueService waitingQueueService;
	private final ProceedQueueService proceedQueueService;
	private final DefaultRedisScript<Long> moveWaitingToProceedScript;
	private final StringRedisTemplate stringRedisTemplate;
	private static final long CAPACITY = 50L;

	public String enterWaitingQueue() {
		return waitingQueueService.enterWaitingQueue();
	}

	public long getWaitingOrder(String token) {
		long waitingOrder = waitingQueueService.getOrder(token);
		long proceedOrder = proceedQueueService.getOrder(token);
		if(waitingOrder == -1 && proceedOrder == -1) {
			log.info("token is : {}", token);
			throw new RuntimeException("올바르지않은 토큰 입니다.");
		}
		//moveWaitingToProceed();
		moveWaitingToProceedAtomic();
		return waitingOrder;
	}

	public boolean isAllowed(String token) {
		return proceedQueueService.isContains(token);
	}

	public void removeTokenFromProceedQueue(String token) {
		proceedQueueService.removeToken(token);
	}

	public void moveWaitingToProceed() {
		long size = proceedQueueService.getSize();
		long remain = CAPACITY - size;
		if(remain > 0) {
			Set<TypedTuple<String>> waitingList = waitingQueueService.popMin(remain);
			if(!waitingList.isEmpty())
				proceedQueueService.enterProceed(waitingList);
		}
	}

	public void moveWaitingToProceedAtomic() {
		stringRedisTemplate.execute(moveWaitingToProceedScript,
			List.of(RedisConst.WAITING_KEY, RedisConst.PROCEED_KEY),
			String.valueOf(CAPACITY)
		);
	}
}
