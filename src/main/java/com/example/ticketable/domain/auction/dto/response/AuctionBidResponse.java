package com.example.ticketable.domain.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionBidResponse {
	private final Integer bidPoint;

	public static AuctionBidResponse of(Integer bidPoint) {
		return new AuctionBidResponse(bidPoint);
	}
}
