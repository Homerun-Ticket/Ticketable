package com.example.ticketable.common.entity;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.role.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class Auth {
	
	private final Long id;
	private final String email;
	private final MemberRole role;
	private final List<? extends GrantedAuthority> authority;
	
	public Auth(Long id, String email, MemberRole role) {
		this.id = id;
		this.email = email;
		this.role = role;
		this.authority = List.of(new SimpleGrantedAuthority(role.name()));
	}
}
