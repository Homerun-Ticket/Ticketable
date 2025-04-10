package com.example.ticketable.domain.auction.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuctionTicketInfoDto {

	private final Integer standardPoint;

	private final String sectionInfo;

	private final String seatInfo;

	private final Integer seatCount;

	private final Boolean isTogether;

	public static AuctionTicketInfoDto of(
		Integer standardPoint, String sectionInfo, String seatInfo,	Integer seatCount, Boolean isTogether
	) {
		return new AuctionTicketInfoDto(
			standardPoint,
			sectionInfo,
			seatInfo,
			seatCount,
			isTogether
		);
	}
}
