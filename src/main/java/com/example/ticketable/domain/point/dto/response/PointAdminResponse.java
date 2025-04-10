package com.example.ticketable.domain.point.dto.response;

import com.example.ticketable.domain.point.enums.PointHistoryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PointAdminResponse {

	private final Long memberId;
	private final Integer charge;
	private final Integer point;
	private final PointHistoryType type;
}
