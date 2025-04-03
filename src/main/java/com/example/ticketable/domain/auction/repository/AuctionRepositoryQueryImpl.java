package com.example.ticketable.domain.auction.repository;

import static com.example.ticketable.domain.auction.entity.QAuction.*;
import static com.example.ticketable.domain.game.entity.QGame.*;
import static com.example.ticketable.domain.stadium.entity.QSeat.*;
import static com.example.ticketable.domain.ticket.entity.QTicket.*;
import static com.example.ticketable.domain.ticket.entity.QTicketPayment.*;
import static com.example.ticketable.domain.ticket.entity.QTicketSeat.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionTicketInfo;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;

import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionRepositoryQueryImpl implements AuctionRepositoryQuery {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public AuctionTicketInfo findTicketInfo(Ticket ticket) {
		return getTicketInfo(ticket);
	}


	private AuctionTicketInfo getTicketInfo(Ticket ticket) {
		Integer standardPoint = jpaQueryFactory
			.select(ticketPayment.totalPoint)
			.from(ticketPayment)
			.where(ticketPayment.ticket.eq(ticket))
			.fetchOne();

		List<Seat> seats = jpaQueryFactory
			.select(ticketSeat.seat)
			.from(ticketSeat)
			.join(ticketSeat.seat, seat)
			.where(ticketSeat.ticket.eq(ticket))
			.fetch();

		String type = seats.get(0).getSection().getType();
		String code = seats.get(0).getSection().getCode();
		String sectionInfo = type + "|" + code;

		String seatInfo = seats.stream()
			.map(seat -> seat.getRowNum() + "-" + seat.getColNum())
			.collect(Collectors.joining(","));

		Boolean isTogether = true;
		String standard = seats.get(0).getRowNum();
		for (Seat seat : seats) {
			if (!standard.equals(seat.getRowNum())) {
				isTogether = false;
				break;
			}
		}

		return AuctionTicketInfo.of(standardPoint, sectionInfo, seatInfo, isTogether);
	}
}
