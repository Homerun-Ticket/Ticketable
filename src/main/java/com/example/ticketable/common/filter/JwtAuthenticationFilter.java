package com.example.ticketable.common.filter;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.util.JwtAuthenticationToken;
import com.example.ticketable.common.util.JwtUtil;
import com.example.ticketable.domain.member.repository.MemberRepository;
import com.example.ticketable.domain.member.role.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException
	{
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("Authorization".equals(cookie.getName())) {
						authorizationHeader = "Bearer " + cookie.getValue();
						break;
					}
				}
			}
		}
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwt = jwtUtil.substringToken(authorizationHeader);
		    try {
				Claims claims = jwtUtil.extractClaims(jwt);
				
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}
			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않는 JWT 서명입니다.", e);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
			} catch (ExpiredJwtException eje) {
				log.error("Expired JWT token, 만료된 JWT 토큰 입니다.", eje);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰 입니다.");
		    } catch (UnsupportedJwtException uje) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", uje);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰 입니다.");
		    }
		}
		filterChain.doFilter(request, response);
	}
	
	private void setAuthentication(Claims claims) {
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		MemberRole role = MemberRole.of(claims.get("role", String.class));
		
		Auth auth = new Auth(userId, email, role);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(auth);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}
