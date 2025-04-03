package com.example.ticketable.domain.ticket.service;

import static com.example.ticketable.common.exception.ErrorCode.TICKET_NOT_FOUND;
import static com.example.ticketable.common.exception.ErrorCode.TICKET_PAYMENT_NOT_FOUND;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.service.SeatService;
import com.example.ticketable.domain.ticket.dto.response.TicketResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.entity.TicketPayment;
import com.example.ticketable.domain.ticket.repository.TicketPaymentRepository;
import com.example.ticketable.domain.ticket.repository.TicketRepository;
import com.example.ticketable.domain.ticket.repository.TicketSeatRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;
	private final TicketSeatRepository ticketSeatRepository;
	private final TicketPaymentRepository ticketPaymentRepository;

	@Transactional(readOnly = true)
	public TicketResponse getTicket(Long ticketId) {
		Ticket ticket = ticketRepository.findByIdWithGame(ticketId)
			.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));

		return convertTicketResponse(ticket);
	}

	@Transactional(readOnly = true)
	public List<TicketResponse> getAllTickets(Auth auth) {
		List<Ticket> allTickets = ticketRepository.findAllByMemberIdWithGame(auth.getId());

		return allTickets.stream().map(this::convertTicketResponse).toList();
	}

	/**
	 * Ticket 을 기준으로 TicketResponse에 필요한 데이터를 가져오고 매핑하는 테이블
	 * @param ticket 엔티티 객체
	 * @return TicketResponse
	 */
	private TicketResponse convertTicketResponse(Ticket ticket) {
		String title = ticket.getGame().getHome() + " vs " + ticket.getGame().getAway();
		log.debug("경기 제목 조회 title: {}", title);
		LocalDateTime startTime = ticket.getGame().getStartTime();
		log.debug("경기 시작 시간 조회 startTime : {}", startTime);
		List<String> ticketSeats = ticketSeatRepository.findByTicketIdWithSeat(ticket.getId()).stream()
			.map(ticketSeat -> {
				Seat seat = ticketSeat.getSeat();
				return seat.getRowNum() + "열 " + seat.getColNum();
			}).toList();
		log.debug("티켓 좌석 조회 ticketSeats: {}", ticketSeats);
		TicketPayment ticketPayment = ticketPaymentRepository.findByTicketId(ticket.getId())
			.orElseThrow(() -> new ServerException(TICKET_PAYMENT_NOT_FOUND));

		int totalPoint = ticketPayment.getTotalPoint();
		log.debug("티켓 결제 금액 조회 ticketPayment: {}", ticketPayment);

		return new TicketResponse(ticket.getId(), title, ticketSeats, startTime, totalPoint);
	}
}
