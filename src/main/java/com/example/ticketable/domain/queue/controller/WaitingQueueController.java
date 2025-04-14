package com.example.ticketable.domain.queue.controller;

import com.example.ticketable.domain.queue.dto.WaitingResponse;
import com.example.ticketable.domain.queue.service.QueueManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WaitingQueueController {
	private final QueueManager queueManager;

	@GetMapping("/v1/waiting-queue/order")
	public ResponseEntity<WaitingResponse> getRemain(@RequestParam(name = "waiting-token") String token) {
		long waitingOrder = queueManager.getWaitingOrder(token);
		String state = waitingOrder > 0 ? "wait" : "allow";
		return ResponseEntity.ok(new WaitingResponse(waitingOrder, state, token));
	}

}
