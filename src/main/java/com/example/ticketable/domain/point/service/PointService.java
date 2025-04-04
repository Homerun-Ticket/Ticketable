package com.example.ticketable.domain.point.service;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.dto.request.AddPointRequest;
import com.example.ticketable.domain.point.dto.response.PointResponse;
import com.example.ticketable.domain.point.entity.Point;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketable.common.exception.ErrorCode.USER_NOT_FOUND;
import static com.example.ticketable.domain.point.enums.PointHistoryType.*;

@RequiredArgsConstructor
@Service
public class PointService {
	
	private final PointRepository pointRepository;
	private final PointHistoryService pointHistoryService;
	
	@Transactional
	public PointResponse addPoint(Long authId, AddPointRequest request) {
		increasePoint(authId, request.getPoint(), FILL);
		return new PointResponse(authId, request.getPoint());
	}
	
	/**
	 * 입찰 실패, 포인트 충전, 환불, 판매 등에서 사용될 포인트 증가 메서드
	 */
	@Transactional
	public void increasePoint(Long authId, Integer charge, PointHistoryType type) {
		Member member = Member.fromAuth(authId);
		Point point = getPoint(authId);
		
		point.plusPoint(charge);
		pointHistoryService.createPointHistory(charge, type, member);
	}
	
	/**
	 * 입찰, 티켓 예매 등에서 사용될 포인트 증가 메서드
	 */
	@Transactional
	public void decreasePoint(Long authId, Integer charge, PointHistoryType type) {
		Member member = Member.fromAuth(authId);
		Point point = getPoint(authId);
		
		point.minusPoint(charge);
		pointHistoryService.createPointHistory(charge, type, member);
	}
	
	/**
	 * 해당 멤버 아이디를 통해 해당 멤버의 포인트를 가져옴.
	 * 만약 멤버가 존재하지 않거나, 삭제되었다면 예외를 던짐
	 */
	private Point getPoint(Long memberId) {
		return pointRepository.findByMemberId(memberId)
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
	}
}
