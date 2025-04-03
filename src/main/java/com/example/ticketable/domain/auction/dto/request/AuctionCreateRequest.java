package com.example.ticketable.domain.auction.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuctionCreateRequest {

	@NotBlank(message = "시작가는 필수값입니다.")
	private Integer startPoint;

	@NotBlank(message = "티켓은 필수값입니다.")
	private Long ticketId;
}
