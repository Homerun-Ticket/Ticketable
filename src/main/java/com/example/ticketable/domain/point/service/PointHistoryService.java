package com.example.ticketable.domain.point.service;

import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.entity.PointHistory;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointHistoryService {
	
	private final PointHistoryRepository pointHistoryRepository;
	
	@Transactional
	public void createPointHistory(Integer charge, PointHistoryType type, Member member) {
		PointHistory pointHistory = PointHistory.builder()
			.charge(charge)
			.type(type)
			.member(member)
			.build();
		
		pointHistoryRepository.save(pointHistory);
	}
}
