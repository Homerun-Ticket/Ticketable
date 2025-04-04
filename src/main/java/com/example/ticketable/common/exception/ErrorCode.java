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

    // 티켓

    // 경매
    AUCTION_DUPLICATION("이미 경매에 등록한 티켓입니다.", CONFLICT),
    AUCTION_ACCESS_DENIED("경매에 대한 권한이 없습니다.", FORBIDDEN),
    AUCTION_TIME_OVER("경매 시간이 지났습니다.", BAD_REQUEST),
    AUCTION_NOT_FOUND("경매를 찾을 수 없습니다", NOT_FOUND),
	INVALID_BIDDING_AMOUNT("입찰액이 잘못되었습니다.", BAD_REQUEST),


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
