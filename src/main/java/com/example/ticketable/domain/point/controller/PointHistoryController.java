package com.example.ticketable.domain.point.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.point.dto.response.PointHistoryResponse;
import com.example.ticketable.domain.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointHistoryController {

	private final PointHistoryService pointHistoryService;
	
	@GetMapping("/api/v1/points")
	public ResponseEntity<PagedModel<PointHistoryResponse>> getPoints(
		@AuthenticationPrincipal Auth auth,
		@RequestParam int page
	) {
		return ResponseEntity.ok(pointHistoryService.getPoints(auth.getId(), page));
	}
}
