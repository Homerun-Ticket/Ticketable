package com.example.ticketable.domain.point.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.point.dto.response.PointAdminResponse;
import com.example.ticketable.domain.point.dto.response.PointHistoryResponse;
import com.example.ticketable.domain.point.service.PointAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PointAdminController {
	
	private final PointAdminService pointAdminService;
	
	@PatchMapping("/v1/admin/pointHistory/{pointHistoryId}/exchange")
	public ResponseEntity<PointAdminResponse> exchangePoint(
		@AuthenticationPrincipal Auth auth,
		@PathVariable Long pointHistoryId
	) {
		return ResponseEntity.ok(pointAdminService.exchangePoint(auth, pointHistoryId));
	}
	
	@GetMapping("/v1/admin/pointHistory/{pointHistoryId}")
	public ResponseEntity<PointHistoryResponse> getAdminPoint(
		@AuthenticationPrincipal Auth auth,
		@PathVariable Long pointHistoryId
	) {
		return ResponseEntity.ok(pointAdminService.getAdminPoint(auth, pointHistoryId));
	}
	
	@GetMapping("/v1/admin/pointHistory")
	public ResponseEntity<PagedModel<PointHistoryResponse>> getAdminPoints(
		@AuthenticationPrincipal Auth auth,
		@RequestParam int page
	) {
		return ResponseEntity.ok(pointAdminService.getAdminPoints(auth, page));
	}
}
