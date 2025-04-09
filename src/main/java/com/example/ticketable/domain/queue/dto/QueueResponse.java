package com.example.ticketable.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueResponse {
	private String message;
	private String status;
	private Long rank;
}
