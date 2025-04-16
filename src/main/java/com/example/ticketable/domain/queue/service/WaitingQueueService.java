package com.example.ticketable.domain.queue.service;

public interface WaitingQueueService {

	//대기열 입장 후 토큰 반환
	String enterWaitingQueue();

	//대기열 순서 조회
	long getOrder(String token);

	//토큰 삭제
	void removeToken(String token);
}
