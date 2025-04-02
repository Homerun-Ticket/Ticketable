package com.example.ticketable.domain.member.entity;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.member.role.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String email;
	
	private String password;
	private String name;
	
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	
	private LocalDateTime deletedAt;
	
	@Builder
	public Member(String email, String password, String name, MemberRole role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = role;
	}
	
	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}
}
