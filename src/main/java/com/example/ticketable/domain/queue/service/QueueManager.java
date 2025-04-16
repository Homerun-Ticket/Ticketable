package com.example.ticketable.domain.queue.service;

import static com.example.ticketable.common.exception.ErrorCode.INVALID_WAITING_TOKEN;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.queue.QueueSystemConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueManager {
	private final WaitingQueueService waitingQueueService;
	private final ProceedQueueService proceedQueueService;

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

	public void deleteTokenFromWaitingAndProceedQueue(String token) {
		waitingQueueService.removeToken(token);
		proceedQueueService.removeToken(token);
	}

	@Scheduled(fixedRate = 1000)
	public void moveWaitingToProceedAtomicScheduled(){
		proceedQueueService.pullFromWaitingQueue(QueueSystemConstants.PROCEED_QUEUE_TARGET_SIZE);
	}

	@Scheduled(fixedRate = 10000)
	public void removeExpiredTokenFromProceedQueue(){
		proceedQueueService.removeExpiredTokens();
	}
}
