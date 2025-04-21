package com.example.ticketable.domain.auction.repository;

import static com.example.ticketable.domain.stadium.entity.QSeat.*;
import static com.example.ticketable.domain.stadium.entity.QSection.*;
import static com.example.ticketable.domain.ticket.entity.QTicketPayment.*;
import static com.example.ticketable.domain.ticket.entity.QTicketSeat.*;

import java.util.List;
import java.util.stream.Collectors;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionTicketInfoRepositoryQueryImpl implements AuctionTicketInfoRepositoryQuery {

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
			.orderBy(seat.position.asc())
			.fetch();

		String type = seats.get(0).getSection().getType();
		String code = seats.get(0).getSection().getCode();
		String sectionInfo = type + " | " + code;

		String seatInfo = seats.stream()
			.map(Seat::getPosition)
			.collect(Collectors.joining(" "));

		Integer seatCount = seats.size();

		Boolean isTogether = false;
		if (seats.size() > 1) {
			String prevRow = null;
			Integer prevColumn = null;
			isTogether = true; // 일단 연석이라고 가정하고, 조건을 깨면 false로 전환

			for (int i = 0; i < seats.size(); i++) {
				String[] parts = seats.get(i).getPosition().split("열 ");
				String currentRow = parts[0];
				int currentColumn = Integer.parseInt(parts[1].replaceAll("\\D", ""));

				if (i == 0) {
					prevRow = currentRow;
					prevColumn = currentColumn;
					continue;
				}

				// 행이 다르거나 열이 연속되지 않으면 연석 아님
				if (!prevRow.equals(currentRow) || currentColumn != prevColumn + 1) {
					isTogether = false;
					break;
				}

				prevColumn = currentColumn;
			}
		}

		return AuctionTicketInfoDto.of(standardPoint, sectionInfo, seatInfo, seatCount, isTogether);
	}
}
