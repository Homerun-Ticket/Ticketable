package com.example.ticketable.domain.queue.service;

public interface ProceedQueueService {
	boolean isContains(String token);

	//작업열에서 토큰 제거
	void removeToken(String token);

	//대기열에서 작업열로 targetSize가 될때까지 이동
	void pullFromWaitingQueue(Long targetSize);

	void removeExpiredTokens();
}
