package com.example.ticketable.domain.ticket.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketCreateRequest {
	private List<Long> seats;
	private Long gameId;
}
