package com.example.ticketable.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteMemberRequest {
	
	@NotNull(message = "비밀번호를 입력해주세요.")
	private String password;
}
