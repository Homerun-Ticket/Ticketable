package com.example.ticketable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
	
	// 경기장
	STADIUM_NOT_FOUND("해당하는 경기장을 찾을 수 없습니다.", NOT_FOUND),
	SECTION_NOT_FOUND("해당하는 구역을 찾을 수 없습니다.", NOT_FOUND),
	SEAT_NOT_FOUND("해당하는 좌석을 찾을 수 없습니다.", NOT_FOUND),
	COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE("열 번호와 시야 방해 여부 리스트의 크기는 같아야 합니다.", BAD_REQUEST),
	BLIND_STATUS_ALREADY_SET("시야 방해석 상태가 이미 요청된 상태와 동일합니다.", BAD_REQUEST),
	SEATS_ALREADY_EXISTS("이미 구역에 좌석이 있습니다.", BAD_REQUEST),

	// 티켓
	
	// 경매
	
	// 포인트
	NOT_ENOUGH_POINT("포인트가 부족합니다.", BAD_REQUEST),
	
	// 유저
	USER_EMAIL_DUPLICATION("다른 유저와 이메일이 중복됩니다.", CONFLICT),
	USER_NOT_LOGIN("로그인이 필요합니다. 로그인을 해주세요.", UNAUTHORIZED),
	USER_NOT_FOUND("해당하는 유저를 찾을 수 없습니다.", NOT_FOUND),
	INVALID_PASSWORD("패스워드가 올바르지 않습니다.", BAD_REQUEST),
	PASSWORD_SAME_AS_OLD("이전 패스워드와 동일할 수 없습니다.", BAD_REQUEST),
	USER_ACCESS_DENIED("사용자가 접근할 수 있는 권한이 없습니다.", FORBIDDEN),
	USER_ROLE_SAME_AS_OLD("이전 역활과 동일할 수 없습니다.", BAD_REQUEST),
	INVALID_USER_ROLE("유효하지 않는 role 입니다.", BAD_REQUEST),
	INVALID_TOKEN("유효하지 않은 토큰입니다.", INTERNAL_SERVER_ERROR);
	
	private final String message;
	private final HttpStatus status;
}
