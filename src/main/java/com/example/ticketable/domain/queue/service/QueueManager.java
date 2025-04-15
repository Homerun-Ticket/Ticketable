package com.example.ticketable.domain.queue.service;

import static com.example.ticketable.common.exception.ErrorCode.INVALID_WAITING_TOKEN;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.queue.RedisConst;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueManager {
	private final WaitingQueueService waitingQueueService;
	private final ProceedQueueService proceedQueueService;
	private final DefaultRedisScript<Long> moveWaitingToProceedScript;
	private final StringRedisTemplate stringRedisTemplate;
	private static final long CAPACITY = 100L;

	//대기열 입장
	public String enterWaitingQueue() {
		return waitingQueueService.enterWaitingQueue();
	}

	//대기순번 조회
	public long getWaitingOrder(String token) {
		long waitingOrder = waitingQueueService.getOrder(token);
		boolean isProceed = proceedQueueService.isContains(token);
		//대기열, 작업열 에 존재하지않으면 잘못된 토큰
		if(waitingOrder == -1 && !isProceed) {
			log.info("token is : {}", token);
			throw new ServerException(INVALID_WAITING_TOKEN);
		}
		return waitingOrder;
	}

	//해당 토큰이 작업이 가능한 상태인지 조회
	public boolean isAllowed(String token) {
		return proceedQueueService.isContains(token);
	}

	public void removeTokenFromProceedQueue(String token) {
		proceedQueueService.removeToken(token);
	}

	//대기열에서 작업열로 CAPACITY로부터 여분만큼 이동
	public void moveWaitingToProceedAtomic() {
		stringRedisTemplate.execute(moveWaitingToProceedScript,
			List.of(RedisConst.WAITING_KEY, RedisConst.PROCEED_KEY),
			String.valueOf(CAPACITY)
		);
	}

	public void deleteTokenFromWaitingAndProceedQueue(String token) {
		waitingQueueService.removeToken(token);
		proceedQueueService.removeToken(token);
	}

	@Scheduled(fixedRate = 1000)
	public void moveWaitingToProceedAtomicScheduled(){
		moveWaitingToProceedAtomic();
	}
}
