package com.example.ticketable.domain.auction.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BidUpdateEvent {

	private final Long auctionId;
	private final int nextBid;
}
