package com.example.ticketable.domain.ticket.service;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.entity.Ticket;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TicketPriceCalculator {

	private static final int WEEKEND_ADDITIONAL_CHARGE = 500;

	public int calculateTicketPrice(Game game, List<Seat> seats) {
		int ticketPrice = game.getPoint() * seats.size();

		DayOfWeek dayOfWeek = game.getStartTime().getDayOfWeek();
		if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
			ticketPrice = ticketPrice + (WEEKEND_ADDITIONAL_CHARGE * seats.size());
		}

		for (Seat seat : seats) {
			ticketPrice += seat.getSection().getExtraCharge();

		}


		return ticketPrice;
	}
}
