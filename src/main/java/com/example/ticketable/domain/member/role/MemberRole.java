package com.example.ticketable.domain.member.role;

import com.example.ticketable.common.exception.ServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

import static com.example.ticketable.common.exception.ErrorCode.INVALID_USER_ROLE;

@Getter
@RequiredArgsConstructor
public enum MemberRole implements GrantedAuthority {
	
	ROLE_MEMBER(Authority.MEMBER),
	ROLE_ADMIN(Authority.ADMIN);
	
	private final String role;
	
	public static MemberRole of(String role) {
		return Arrays.stream(MemberRole.values())
			.filter(f -> f.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new ServerException(INVALID_USER_ROLE));
	}
	
	@Override
	public String getAuthority() {
		return name();
	}
	
	public static class Authority {
		public static final String MEMBER = "ROLE_MEMBER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}
