package com.example.ticketable.domain.auction.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuctionBidRequest {

	@NotBlank(message = "입찰가는 필수값입니다.")
	private Integer bidPoint;
}
