package com.example.ticketable.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {
	
	@NotNull(message = "이메일을 입력해주세요.")
	@Email
	private String email;
	
	@NotNull(message = "이름을 입력해주세요.")
	private String name;
	
	@NotNull(message = "비밀번호는 8~12자의 영문, 숫자, 특수문자를 포함해야 합니다.")
	@Pattern(
		message = "비밀번호는 8~12자의 영문, 숫자, 특수문자를 포함해야 합니다.",
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$"
	)
	private String password;
	
	@NotNull(message = "비밀번호를 재입력 해주세요.")
	private String rePassword;
	
	@NotNull(message = "역할을 입력해주세요.")
	private String role;
	
	public boolean validRePassword() {
		return this.password.equals(this.rePassword);
	}
}
