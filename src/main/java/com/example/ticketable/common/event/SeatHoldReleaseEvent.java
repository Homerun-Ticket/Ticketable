package com.example.ticketable.common.event;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeatHoldReleaseEvent {
	private final List<Long> seatIds;
	private final Long gameId;

}
