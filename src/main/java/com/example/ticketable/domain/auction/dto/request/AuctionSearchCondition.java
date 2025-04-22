package com.example.ticketable.domain.auction.dto.request;

import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchCondition {

	@Nullable
	private String home;

	@Nullable
	private String away;

	@Nullable
	private Integer seatCount;

	@Nullable
	private Boolean isTogether = false;

	@Nullable
	private LocalDateTime startTime;
}
