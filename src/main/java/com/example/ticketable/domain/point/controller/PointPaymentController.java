package com.example.ticketable.domain.point.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.point.service.PointPaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PointPaymentController {
	
	private final PointPaymentService pointPaymentService;
	
	@PostMapping("/v1/payments/{imp_uid}")
	public ResponseEntity<IamportResponse<Payment>> iamPortPayment(
		@AuthenticationPrincipal Auth auth,
		@PathVariable String imp_uid
	) {
		log.info("imp_uid = {}", imp_uid);
		return ResponseEntity.ok(pointPaymentService.iamPortPayment(auth, imp_uid));
	}
}
