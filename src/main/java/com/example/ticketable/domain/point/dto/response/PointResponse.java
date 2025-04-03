package com.example.ticketable.domain.point.dto.response;

import com.example.ticketable.domain.point.entity.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PointResponse {
	
	private final Long id;
	private final Long memberId;
	private final Integer point;
	
	public static PointResponse of(Point point) {
		return new PointResponse(
			point.getId(),
			point.getMember().getId(),
			point.getPoint()
		);
	}
}
