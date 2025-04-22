package com.example.ticketable.domain.point.service;

import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.dto.response.PointHistoryResponse;
import com.example.ticketable.domain.point.entity.PointHistory;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
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
	
	@Transactional(readOnly = true)
	public PagedModel<PointHistoryResponse> getPointHistories(Long authId, int page) {
		Pageable pageable = PageRequest.of(page - 1, 10,
			Sort.by(Sort.Direction.DESC, "createdAt"));
		
		Page<PointHistory> points = pointHistoryRepository.findAllByMemberId(authId, pageable);
		
		return new PagedModel<>(points.map(PointHistoryResponse::of));
	}
}
