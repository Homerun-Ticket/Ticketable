package com.example.ticketable.domain.auction.repository;

import static com.example.ticketable.domain.auction.entity.QAuction.*;
import static com.example.ticketable.domain.auction.entity.QAuctionTicketInfo.*;
import static com.example.ticketable.domain.game.entity.QGame.*;
import static com.example.ticketable.domain.stadium.entity.QSeat.*;
import static com.example.ticketable.domain.stadium.entity.QSection.*;
import static com.example.ticketable.domain.ticket.entity.QTicket.*;
import static com.example.ticketable.domain.ticket.entity.QTicketPayment.*;
import static com.example.ticketable.domain.ticket.entity.QTicketSeat.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionRepositoryQueryImpl implements AuctionRepositoryQuery {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<Auction> findByConditions(AuctionSearchCondition dto, Pageable pageable) {
		BooleanExpression homeEq = dto.getHome() != null ? auction.ticket.game.home.eq(dto.getHome()) : null;
		BooleanExpression awayEq = dto.getAway() != null ? auction.ticket.game.away.eq(dto.getAway()) : null;
		BooleanExpression startTimeBetween = dto.getStartTime() != null
			? auction.ticket.game.startTime.between(
			dto.getStartTime().toLocalDate().atStartOfDay(),
			dto.getStartTime().toLocalDate().atStartOfDay().plusDays(1).minusSeconds(1)
		)
			: null;
		BooleanExpression seatCountEq =
			dto.getSeatCount() != null ? auction.auctionTicketInfo.seatCount.eq(dto.getSeatCount()) : null;
		BooleanExpression isTogether = dto.getIsTogether()
			? auction.auctionTicketInfo.isTogether.isTrue()
			: auction.auctionTicketInfo.isTogether.isFalse();

		List<Auction> results = jpaQueryFactory
			.selectFrom(auction)
			.join(auction.ticket, ticket)
			.join(ticket.game, game)
			.join(auction.auctionTicketInfo, auctionTicketInfo)
			.where(homeEq, awayEq, startTimeBetween, seatCountEq, isTogether)
			.offset(pageable.getPageNumber())
			.limit(pageable.getPageSize())
			.orderBy(auction.createdAt.asc())
			.fetch();

		Long total = jpaQueryFactory
			.select(auction.countDistinct())
			.from(auction)
			.where(homeEq, awayEq, startTimeBetween, seatCountEq, isTogether)
			.fetchOne();

		return new PageImpl<>(results, pageable, total != null ? total : 0L);
	}
}
