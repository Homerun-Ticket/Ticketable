package com.example.ticketable.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerException extends RuntimeException {
	private final ErrorCode errorCode;
}
