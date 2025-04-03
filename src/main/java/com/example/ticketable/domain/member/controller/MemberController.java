package com.example.ticketable.domain.member.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.member.dto.request.DeleteMemberRequest;
import com.example.ticketable.domain.member.dto.request.UpdatePasswordRequest;
import com.example.ticketable.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
	
	private final MemberService memberService;
	
	@PatchMapping
	public ResponseEntity<String> updatePassword(
		@AuthenticationPrincipal Auth auth,
		@Valid @RequestBody UpdatePasswordRequest request
	) {
		memberService.updatePassword(auth.getId(), request);
		return ResponseEntity.ok("비밀번호가 변경되었습니다.");
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteMember(
		@AuthenticationPrincipal Auth auth,
		@Valid @RequestBody DeleteMemberRequest request
	) {
		memberService.deleteMember(auth.getId(), request);
		return ResponseEntity.ok("멤버가 삭제되었습니다.");
	}
}
