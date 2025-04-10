package com.example.ticketable.domain.auction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionCreateRequest {

	@NotNull(message = "시작가는 필수값입니다.")
	private Integer startPoint;

	@NotNull(message = "티켓은 필수값입니다.")
	private Long ticketId;
}
