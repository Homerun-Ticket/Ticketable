package com.example.ticketable.domain.queue.service;

import com.example.ticketable.domain.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
	private final QueueRepository queueRepository;
	private static final String KEY_PREFIX ="game";
	private static final int CAPACITY = 100;

	//작업을 수행할수있는지 확인
	public boolean canJob(Long gameId, Long userId) {
		String key = createKey(gameId);
		String value = String.valueOf(userId);

		//대기열에 존재하지않으면 대기열에 추가
		if(!queueRepository.isContains(key, value)) {
			log.info("add");
			queueRepository.addQueue(key, value);
		}

		//순서 조회후 상위 100개의 순서는 작업 허용
		Long rank = queueRepository.getRank(key, value);
		if(rank != null && rank < CAPACITY) {
			return true;
		}

		return false;
	}

	public Long getRank(Long gameId, Long userId) {
		String key = createKey(gameId);
		String value = String.valueOf(userId);
		return queueRepository.getRank(key, value);
	}


	public String createKey(Long gameId) {
		return KEY_PREFIX+":"+gameId + ":queue";
	}

	public void delete(Long gameId, Long userId) {
		String key = createKey(gameId);
		String value = String.valueOf(userId);
		queueRepository.delete(key, value);
	}

}
