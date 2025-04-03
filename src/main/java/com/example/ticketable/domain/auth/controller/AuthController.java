package com.example.ticketable.domain.auth.controller;

import com.example.ticketable.domain.auth.dto.request.LoginRequest;
import com.example.ticketable.domain.auth.dto.request.SignupRequest;
import com.example.ticketable.domain.auth.dto.response.AuthResponse;
import com.example.ticketable.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
		return ResponseEntity.ok(authService.signup(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
