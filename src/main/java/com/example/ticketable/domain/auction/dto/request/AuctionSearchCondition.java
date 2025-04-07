package com.example.ticketable.domain.auction.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchCondition {

	private String home;

	private String away;

	private Integer seatCount;

	private Boolean isTogether = false;

	private LocalDateTime startTime;
}
