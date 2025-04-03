package com.example.ticketable.domain.auction.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuctionSearchCondition {

	@NotBlank(message = "홈 팀은 필수값입니다.")
	private String home;

	private String away;

	private Integer seatCount;

	private Boolean isTogether;

	private LocalDateTime startTime;
}
