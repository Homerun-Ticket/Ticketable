package com.example.ticketable.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {
	
	@NotNull(message = "이메일을 입력해주세요.")
	private String email;
	
	@NotNull(message = "비밀번호를 입력해주세요.")
	private String password;
}
