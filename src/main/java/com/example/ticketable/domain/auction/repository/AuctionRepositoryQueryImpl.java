package com.example.ticketable.domain.auction.repository;

import static com.example.ticketable.domain.auction.entity.QAuction.*;
import static com.example.ticketable.domain.auction.entity.QAuctionTicketInfo.*;
import static com.example.ticketable.domain.game.entity.QGame.*;
import static com.example.ticketable.domain.stadium.entity.QSeat.*;
import static com.example.ticketable.domain.stadium.entity.QSection.*;
import static com.example.ticketable.domain.ticket.entity.QTicket.*;
import static com.example.ticketable.domain.ticket.entity.QTicketPayment.*;
import static com.example.ticketable.domain.ticket.entity.QTicketSeat.*;

import java.util.List;
import java.util.stream.Collectors;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;

import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionRepositoryQueryImpl implements AuctionRepositoryQuery {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public AuctionTicketInfoDto findTicketInfo(Ticket ticket) {
		Integer standardPoint = jpaQueryFactory
			.select(ticketPayment.totalPoint)
			.from(ticketPayment)
			.where(ticketPayment.ticket.eq(ticket))
			.fetchOne();

		List<Seat> seats = jpaQueryFactory
			.select(ticketSeat.seat)
			.from(ticketSeat)
			.join(ticketSeat.seat, seat)
			.join(seat.section, section)
			.where(ticketSeat.ticket.eq(ticket))
			.fetch();

		String type = seats.get(0).getSection().getType();
		String code = seats.get(0).getSection().getCode();
		String sectionInfo = type + " | " + code;

		String seatInfo = seats.stream()
			.map(Seat::getPosition)
			.collect(Collectors.joining(" "));

		Integer seatCount = seats.size();

		Boolean isTogether = true;
		String firstRow = seats.get(0).getPosition().split("열 ")[0];
		for (Seat seat : seats) {
			String currentRow = seat.getPosition().split("열 ")[0];
			if (!firstRow.equals(currentRow)) {
				isTogether = false;
				break;
			}
		}

		return AuctionTicketInfoDto.of(standardPoint, sectionInfo, seatInfo, seatCount, isTogether);
	}

	@Override
	public PagedModel<AuctionResponse> findByConditions(AuctionSearchCondition dto, Pageable pageable) {
		List<AuctionResponse> results = jpaQueryFactory
			.select(
				Projections.constructor(AuctionResponse.class,
					auction.id,
					auction.startPoint,
					auction.bidPoint,
					auctionTicketInfo.standardPoint,
					auctionTicketInfo.sectionInfo,
					auctionTicketInfo.seatInfo,
					auctionTicketInfo.seatCount,
					auctionTicketInfo.isTogether,
					game.startTime,
					game.home,
					game.away,
					game.type.stringValue(),
					auction.createdAt,
					auction.updatedAt,
					auction.deletedAt
				)
			)
			.from(auction)
			.join(auction.ticket, ticket)
			.join(ticket.game, game)
			.join(auction.auctionTicketInfo, auctionTicketInfo)
			.where(
				dto.getHome() != null ? auction.ticket.game.home.eq(dto.getHome()) : null,
				dto.getAway() != null ? auction.ticket.game.away.eq(dto.getAway()) : null,
				dto.getStartTime() != null ? auction.ticket.game.startTime.between(
					dto.getStartTime().toLocalDate().atStartOfDay(),
					dto.getStartTime().toLocalDate().atStartOfDay().plusDays(1).minusSeconds(1)
				) : null,
				dto.getSeatCount() != null ? auctionTicketInfo.seatCount.eq(dto.getSeatCount()) : null,
				dto.getIsTogether() ? auctionTicketInfo.isTogether.isTrue() : auctionTicketInfo.isTogether.isFalse()
			)
			.offset(pageable.getPageNumber())
			.limit(pageable.getPageSize())
			.orderBy(auction.createdAt.asc())
			.fetch();

		Long total = jpaQueryFactory
			.select(auction.countDistinct())
			.from(auction)
			.where(
				dto.getHome() != null ? auction.ticket.game.home.eq(dto.getHome()) : null,
				dto.getAway() != null ? auction.ticket.game.away.eq(dto.getAway()) : null,
				dto.getStartTime() != null ? auction.ticket.game.startTime.between(
					dto.getStartTime().toLocalDate().atStartOfDay(),
					dto.getStartTime().toLocalDate().atStartOfDay().plusDays(1).minusSeconds(1)
				) : null,
				dto.getSeatCount() != null ? auctionTicketInfo.seatCount.eq(dto.getSeatCount()) : null,
				dto.getIsTogether() ? auctionTicketInfo.isTogether.isTrue() : auctionTicketInfo.isTogether.isFalse()
			)
			.fetchOne();

		PageImpl<AuctionResponse> auctionResponses = new PageImpl<>(results, pageable, total != null ? total : 0L);

		return new PagedModel<>(auctionResponses);
	}
}
