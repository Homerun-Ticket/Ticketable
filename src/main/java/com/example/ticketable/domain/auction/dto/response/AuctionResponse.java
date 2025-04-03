package com.example.ticketable.domain.auction.dto.response;

import java.time.LocalDateTime;

import com.example.ticketable.domain.auction.entity.Auction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuctionResponse {

	private final Long id;

	private final Integer startPoint;

	private final Integer bidPoint;

	private final Integer standardPoint;

	private final String sectionInfo;

	private final String seatInfo;

	private final Boolean isTogether;

	private final LocalDateTime gameStartTime;

	private final String home;

	private final String away;

	private final String type;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	private final LocalDateTime deletedAt;

	public static AuctionResponse of(Auction auction, AuctionTicketInfo ticketInfo) {
		return new AuctionResponse(
			auction.getId(),
			auction.getStartPoint(),
			auction.getBidPoint(),
			ticketInfo.getStandardPoint(),
			ticketInfo.getSectionInfo(),
			ticketInfo.getSeatInfo(),
			ticketInfo.getIsTogether(),
			auction.getTicket().getGame().getStartTime(),
			auction.getTicket().getGame().getHome(),
			auction.getTicket().getGame().getAway(),
			auction.getTicket().getGame().getType().toString(),
			auction.getCreatedAt(),
			auction.getUpdatedAt(),
			auction.getDeletedAt()
		);
	}
}
