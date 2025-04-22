package com.example.ticketable.domain.auction.dto.response;

import java.time.LocalDateTime;

import com.example.ticketable.domain.auction.entity.Auction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuctionResponse {

	private final Long id;

	private final Integer startPoint;

	private final Integer bidPoint;

	private final Integer standardPoint;

	private final String sectionInfo;

	private final String seatInfo;

	private final Integer seatCount;

	private final Boolean isTogether;

	private final LocalDateTime gameStartTime;

	private final String home;

	private final String away;

	private final String type;

	private final LocalDateTime createdAt;


	public static AuctionResponse of(Auction auction) {
		return new AuctionResponse(
			auction.getId(),
			auction.getStartPoint(),
			auction.getBidPoint(),
			auction.getAuctionTicketInfo().getStandardPoint(),
			auction.getAuctionTicketInfo().getSectionInfo(),
			auction.getAuctionTicketInfo().getSeatInfo(),
			auction.getAuctionTicketInfo().getSeatCount(),
			auction.getAuctionTicketInfo().getIsTogether(),
			auction.getTicket().getGame().getStartTime(),
			auction.getTicket().getGame().getHome(),
			auction.getTicket().getGame().getAway(),
			auction.getTicket().getGame().getType().toString(),
			auction.getCreatedAt()
		);
	}
}
