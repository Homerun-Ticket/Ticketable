package com.example.ticketable.domain.ticket.service;

import static com.example.ticketable.common.exception.ErrorCode.TICKET_PAYMENT_NOT_FOUND;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.service.PointService;
import com.example.ticketable.domain.ticket.dto.TicketContext;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.entity.TicketPayment;
import com.example.ticketable.domain.ticket.repository.TicketPaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TicketPaymentService {

	private final TicketPaymentRepository ticketPaymentRepository;
	private PointService pointService;

	@Transactional
	public void paymentTicket(TicketContext ticketContext) {
		pointService.decreasePoint(ticketContext.getMember().getId(), ticketContext.getTotalPoint(),
			PointHistoryType.RESERVATION);
		create(ticketContext.getTicket(), ticketContext.getMember(), ticketContext.getTotalPoint());
	}

	public void create(Ticket ticket, Member member, int point) {
		TicketPayment ticketPayment = new TicketPayment(point, ticket, member);
		ticketPaymentRepository.save(ticketPayment);
	}

	public int getTicketTotalPoint(Long ticketId) {
		return ticketPaymentRepository.findByTicketId(ticketId)
			.orElseThrow(() -> new ServerException(TICKET_PAYMENT_NOT_FOUND))
			.getTotalPoint();
	}
}
