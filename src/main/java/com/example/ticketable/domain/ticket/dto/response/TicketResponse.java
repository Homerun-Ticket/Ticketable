package com.example.ticketable.domain.ticket.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketResponse {
	private final Long ticketId;
	private final String title;
	private final List<String> seats;
	private final LocalDateTime startTime;
	private final Integer totalPoint;
}
