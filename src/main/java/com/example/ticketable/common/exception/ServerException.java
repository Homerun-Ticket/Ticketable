package com.example.ticketable.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
	private final ErrorCode errorCode;

	public ServerException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
