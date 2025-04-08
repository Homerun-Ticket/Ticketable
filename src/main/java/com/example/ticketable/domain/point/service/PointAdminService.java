package com.example.ticketable.domain.point.service;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.dto.response.PointAdminResponse;
import com.example.ticketable.domain.point.dto.response.PointHistoryResponse;
import com.example.ticketable.domain.point.entity.Point;
import com.example.ticketable.domain.point.entity.PointHistory;
import com.example.ticketable.domain.point.repository.PointHistoryRepository;
import com.example.ticketable.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketable.common.exception.ErrorCode.*;
import static com.example.ticketable.domain.member.role.MemberRole.ROLE_ADMIN;
import static com.example.ticketable.domain.point.enums.PointHistoryType.EXCHANGE_REQUEST;

@Service
@RequiredArgsConstructor
public class PointAdminService {
	
	private final PointHistoryRepository pointHistoryRepository;
	private final PointRepository pointRepository;
	
	@Transactional
	public PointAdminResponse exchangePoint(Long authId, Long pointHistoryId) {
		checkAdmin(authId);
		
		PointHistory pointHistory = pointHistoryRepository.findById(pointHistoryId)
			.orElseThrow(() -> new ServerException(POINT_HISTORY_NOT_FOUND));
		
		if (!pointHistory.getType().equals(EXCHANGE_REQUEST)) {
			throw new ServerException(EXCHANGE_REQUEST_NOT_STATE);
		}
		
		Long memberId = pointHistory.getMember().getId();
		Point point = pointRepository.findByMemberId(memberId)
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
		
		pointHistory.exchange();
		point.minusPoint(pointHistory.getCharge());
		
		return new PointAdminResponse(memberId, pointHistory.getCharge(), point.getPoint(), pointHistory.getType());
	}
	
	@Transactional(readOnly = true)
	public PointHistoryResponse getAdminPoint(Long authId, Long pointHistoryId) {
		checkAdmin(authId);
		PointHistory pointHistory = getPointHistory(pointHistoryId);
		
		return PointHistoryResponse.of(pointHistory);
	}
	
	@Transactional(readOnly = true)
	public PagedModel<PointHistoryResponse> getAdminPoints(Long authId, int page) {
		checkAdmin(authId);
		
		Pageable pageable = PageRequest.of(page - 1, 10,
			Sort.by(Sort.Direction.ASC, "createdAt"));
		
		Page<PointHistory> points = pointHistoryRepository.findAllByType(EXCHANGE_REQUEST, pageable);
		
		return new PagedModel<>(points.map(PointHistoryResponse::of));
	}
	
	private void checkAdmin(Long memberId) {
		Member member = Member.fromAuth(memberId);
		if (!member.getRole().equals(ROLE_ADMIN)) {
			throw new ServerException(USER_ACCESS_DENIED);
		}
	}
	
	private PointHistory getPointHistory(Long pointHistoryId) {
		return pointHistoryRepository.findById(pointHistoryId)
			.orElseThrow(() -> new ServerException(POINT_HISTORY_NOT_FOUND));
	}
}
