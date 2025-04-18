package com.example.ticketable.domain.queue;

public class QueueSystemConstants {
	public static final String PROCEED_QUEUE_KEY = "proceed";
	public static final String WAITING_QUEUE_KEY = "waiting";
	public static final long TOKEN_EXPIRES = 1000L*60*5;
	public static final long PROCEED_QUEUE_TARGET_SIZE = 100L;
	public static final String WAITING_QUEUE_HEADER_NAME = "waiting-token";
	public static final long MAX_POLLING_TIME = 5L;
}
