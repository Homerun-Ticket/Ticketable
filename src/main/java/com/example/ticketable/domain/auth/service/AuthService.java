package com.example.ticketable.domain.auth.service;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.util.JwtUtil;
import com.example.ticketable.domain.auth.dto.request.LoginRequest;
import com.example.ticketable.domain.auth.dto.request.SignupRequest;
import com.example.ticketable.domain.auth.dto.response.AuthResponse;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.member.repository.MemberRepository;
import com.example.ticketable.domain.member.role.MemberRole;
import com.example.ticketable.domain.point.entity.Point;
import com.example.ticketable.domain.point.repository.PointRepository;
import com.example.ticketable.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketable.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final PointService pointService;
	
	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (!request.validRePassword()) {
			throw new ServerException(INVALID_PASSWORD);
		}
		
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new ServerException(USER_EMAIL_DUPLICATION);
		}
		
		Member member = Member.builder()
			.email(request.getEmail())
			.name(request.getName())
			.password(passwordEncoder.encode(request.getPassword()))
			.role(MemberRole.of(request.getRole()))
			.build();
		Member savedMember = memberRepository.save(member);
		System.out.println("[DEBUG] member 저장 완료: " + savedMember.getId());
		pointService.createPoint(savedMember);
		System.out.println("[DEBUG] point 저장 완료");
		String accessToken = jwtUtil.createAccessToken(
			savedMember.getId(), savedMember.getEmail(), savedMember.getName(), savedMember.getRole()
		);
		return new AuthResponse(accessToken);
	}
	
	@Transactional
	public AuthResponse login(LoginRequest request) {
		Member findMember = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
		
		if (!passwordEncoder.matches(request.getPassword(), findMember.getPassword())) {
			throw new ServerException(INVALID_PASSWORD);
		}
		
		String accessToken = jwtUtil.createAccessToken(
			findMember.getId(), findMember.getEmail(), findMember.getName(), findMember.getRole()
		);
		return new AuthResponse(accessToken);
	}
}
