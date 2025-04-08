package com.example.ticketable.domain.point.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.point.dto.request.AddPointRequest;
import com.example.ticketable.domain.point.dto.request.ExchangePointRequest;
import com.example.ticketable.domain.point.dto.response.PointResponse;
import com.example.ticketable.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PointController {
	
	private final PointService pointService;
	
	@PostMapping("/v1/points")
	public ResponseEntity<PointResponse> addPoint(
		@AuthenticationPrincipal Auth auth,
		@Valid @RequestBody AddPointRequest request
	) {
		return ResponseEntity.ok(pointService.addPoint(auth.getId(), request));
	}
	
	@PostMapping("/v1/points")
	public ResponseEntity<PointResponse> exchangePoint(
		@AuthenticationPrincipal Auth auth,
		@Valid @RequestBody ExchangePointRequest request
	) {
		return ResponseEntity.ok(pointService.exchangePoint(auth.getId(), request));
	}
}
