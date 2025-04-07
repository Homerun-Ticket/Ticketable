package com.example.ticketable.domain.stadium.dto.request;

import jakarta.validation.GroupSequence;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatHoldRequest {
	private List<Long> seatIds;
	private Long gameId;
}
