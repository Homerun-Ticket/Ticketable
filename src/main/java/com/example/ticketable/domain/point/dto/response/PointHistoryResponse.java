package com.example.ticketable.domain.point.dto.response;

import com.example.ticketable.domain.point.entity.PointHistory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistoryResponse {
	
	private final Long memberId;
	private final String type;
	private final Integer charge;
	private final LocalDateTime createdAt;
	
	public static PointHistoryResponse of(PointHistory pointHistory) {
		return new PointHistoryResponse(
			pointHistory.getMember().getId(),
			pointHistory.getType().toString(),
			pointHistory.getCharge(),
			pointHistory.getCreatedAt()
		);
	}
}
