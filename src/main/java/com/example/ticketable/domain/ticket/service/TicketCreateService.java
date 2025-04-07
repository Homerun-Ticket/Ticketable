package com.example.ticketable.domain.ticket.service;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.util.SeatHoldRedisUtil;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.service.GameService;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.service.SeatService;
import com.example.ticketable.domain.ticket.dto.TicketContext;
import com.example.ticketable.domain.ticket.dto.request.TicketCreateRequest;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.repository.TicketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketCreateService {

	private final TicketRepository ticketRepository;
	private final TicketSeatService ticketSeatService;
	private final SeatService seatService;
	private final GameService gameService;
	private final TicketPriceCalculator ticketPriceCalculator;
	private final SeatHoldRedisUtil seatHoldRedisUtil;

	//티켓 생성
	@Transactional
	public TicketContext createTicket(Auth auth, TicketCreateRequest ticketCreateRequest) {

		ticketSeatService.checkDuplicateSeats(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId());

		List<Seat> seats = seatService.getAllSeatEntity(ticketCreateRequest.getSeats());
		Game game = gameService.getGameEntity(ticketCreateRequest.getGameId());

		Member member = Member.fromAuth(auth.getId());
		Ticket ticket = ticketRepository.save(new Ticket(member, game));
		ticketSeatService.createAll(seats, game, ticket);

		int totalPrice = ticketPriceCalculator.calculateTicketPrice(game, seats);

		return new TicketContext(ticket, member, game, seats, totalPrice);
	}

	@Transactional
	public TicketContext createTicketV2(Auth auth, TicketCreateRequest ticketCreateRequest) {

		seatHoldRedisUtil.checkHeldSeat(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId(), String.valueOf(auth.getId()));
		try {
			ticketSeatService.checkDuplicateSeats(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId());
		} catch (ServerException e) {
			seatHoldRedisUtil.releaseSeatAtomic(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId());
			throw e;
		}

		List<Seat> seats = seatService.getAllSeatEntity(ticketCreateRequest.getSeats());
		Game game = gameService.getGameEntity(ticketCreateRequest.getGameId());

		Member member = Member.fromAuth(auth.getId());
		Ticket ticket = ticketRepository.save(new Ticket(member, game));
		ticketSeatService.createAll(seats, game, ticket);

		int totalPrice = ticketPriceCalculator.calculateTicketPrice(game, seats);

		return new TicketContext(ticket, member, game, seats, totalPrice);
	}

	//생성된 티켓 롤백
	@Transactional
	public void rollBackTicket(Ticket ticket) {
		ticketSeatService.deleteAllTicketSeats(ticket.getId());
		ticketRepository.delete(ticket);
	}

}
