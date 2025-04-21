package com.example.ticketable.domain.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionBidRequest {

	@NotNull(message = "기준입찰가는 필수값입니다.")
	private Integer currentBidPoint;
}
