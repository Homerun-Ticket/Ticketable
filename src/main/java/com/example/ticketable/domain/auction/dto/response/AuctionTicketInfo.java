package com.example.ticketable.domain.auction.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuctionTicketInfo {

	private final Integer standardPoint;

	private final String sectionInfo;

	private final String seatInfo;

	private final Boolean isTogether;

	public static AuctionTicketInfo of(Integer standardPoint, String sectionInfo, String seatInfo, Boolean isTogether) {
		return new AuctionTicketInfo(
			standardPoint,
			sectionInfo,
			seatInfo,
			isTogether
		);
	}
}
