package com.example.ticketable.domain.auction.controller;

import static com.example.ticketable.common.util.Util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.auction.dto.request.AuctionBidRequest;
import com.example.ticketable.domain.auction.dto.request.AuctionCreateRequest;
import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.auction.service.AuctionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

	private final AuctionService auctionService;

	@PostMapping
	public ResponseEntity<AuctionResponse> createAuction(
		@AuthenticationPrincipal Auth auth,
		@Valid @RequestBody AuctionCreateRequest dto
	) {
		return ResponseEntity.ok(auctionService.createAuction(auth, dto));
	}

	@GetMapping("/{auctionId}")
	public ResponseEntity<AuctionResponse> getAuction(
		@PathVariable Long auctionId
	) {
		return ResponseEntity.ok(auctionService.getAuction(auctionId));
	}

	@GetMapping
	public ResponseEntity<Page<AuctionResponse>> getAuctions(
		@ModelAttribute AuctionSearchCondition dto,
		@PageableDefault(page = 1, size = 10) Pageable pageRequest
	) {
		return ResponseEntity.ok(auctionService.getAuctions(dto, convertPageable(pageRequest)));
	}

	@PostMapping("/{auctionId}")
	public ResponseEntity<AuctionResponse> bidAuction(
		@AuthenticationPrincipal Auth auth,
		@PathVariable Long auctionId,
		@RequestBody AuctionBidRequest dto
	) {
		return ResponseEntity.ok(auctionService.bidAuction(auth, auctionId, dto));
	}

}
