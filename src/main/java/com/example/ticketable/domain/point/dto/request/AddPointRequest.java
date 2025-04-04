package com.example.ticketable.domain.point.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddPointRequest {
	
	@NotNull(message = "포인트를 입력해주세요.")
	private Integer point;
}
