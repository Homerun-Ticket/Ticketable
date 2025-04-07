package com.example.ticketable.domain.ticket.dto;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.dto.response.TicketResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketContext {
	private final Ticket ticket;
	private final Member member;
	private final Game game;
	private final List<Seat> seats;
	private final int totalPoint;

	public TicketResponse toResponse() {
		return new TicketResponse(
			ticket.getId(),
			game.getHome() + " vs " + game.getAway(),
			seats.stream().map(Seat::getPosition).toList(),
			game.getStartTime(),
			totalPoint
		);
	}
}
