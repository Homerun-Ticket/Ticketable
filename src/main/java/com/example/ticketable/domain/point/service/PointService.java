package com.example.ticketable.domain.point.service;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.member.repository.MemberRepository;
import com.example.ticketable.domain.point.dto.request.AddPointRequest;
import com.example.ticketable.domain.point.dto.response.PointResponse;
import com.example.ticketable.domain.point.entity.Point;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketable.common.exception.ErrorCode.HAVE_NOT_POINT;
import static com.example.ticketable.common.exception.ErrorCode.USER_NOT_FOUND;
import static com.example.ticketable.domain.point.enums.PointHistoryType.*;

@RequiredArgsConstructor
@Service
public class PointService {
	
	private final MemberRepository memberRepository;
	private final PointRepository pointRepository;
	private final PointHistoryService pointHistoryService;
	
	@Transactional
	public PointResponse addPoint(Long authId, AddPointRequest request) {
		Member member = getMember(authId);
		
		if (memberHaveNotPoint(member.getId())) {
			Point point = Point.builder()
				.point(request.getPoint())
				.member(member)
				.build();
			
			Point savedPoint = pointRepository.save(point);
			pointHistoryService.createPointHistory(request.getPoint(), FILL, member);
			return PointResponse.of(savedPoint);
		}
		
		Point point = getPoint(member.getId());
		
		point.increasePoint(request.getPoint());
		
		pointHistoryService.createPointHistory(request.getPoint(), FILL, member);
		
		return PointResponse.of(point);
	}
	
	/**
	 * 경매, 티켓 예매, 포인트 충전, 환불 등에서 사용될 포인트 증감 메서드
	 */
	@Transactional
	public void increaseDecreasePoint(Long authId, Integer charge, PointHistoryType type) {
		Member member = getMember(authId);
		
		if (memberHaveNotPoint(member.getId())) {
			throw new ServerException(HAVE_NOT_POINT);
		}
		Point point = getPoint(member.getId());
		
		if (type.equals(REFUND) || type.equals(BID_REFUND) || type.equals(SELL)) {
			point.increasePoint(charge);
		} else {
			point.decreasePoint(charge);
		}
		pointHistoryService.createPointHistory(charge, type, member);
	}
	
	/**
	 * 해당 멤버 아이디를 통해 멤버를 가져옴.
	 * 만약 해당 멤버가 존재하지 않거나, 삭제되었다면 예외를 던짐
	 */
	private Member getMember(Long memberId) {
		return memberRepository.findMemberById(memberId)
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
	}
	
	/**
	 * 해당 멤버에게 포인트가 있는지 확인하는 메서드
	 */
	private boolean memberHaveNotPoint(Long memberId) {
		return !pointRepository.existsByMemberId(memberId);
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
