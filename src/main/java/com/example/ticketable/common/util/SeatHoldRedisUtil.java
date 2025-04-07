package com.example.ticketable.common.util;

import static com.example.ticketable.common.exception.ErrorCode.TICKET_ALREADY_RESERVED;

import com.example.ticketable.common.exception.ServerException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatHoldRedisUtil {
	private final RedisTemplate<String, String> redisTemplate;
	private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(15);
	private static final String SEAT_HOLD_TTL_STRING = String.valueOf(SEAT_HOLD_TTL.getSeconds());
	private static final DefaultRedisScript<Long> defaultRedisScript;

	static {
		defaultRedisScript = new DefaultRedisScript<>();
		defaultRedisScript.setResultType(Long.class);
	}

	public void holdSeat(List<Long> seatIds, Long gameId, String value) {
		for (Long seatId : seatIds) {
			String key = createKey(seatId, gameId);
			Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, value, SEAT_HOLD_TTL);
			if (Boolean.FALSE.equals(isNew)) {
				throw new ServerException(TICKET_ALREADY_RESERVED);
			}
		}
	}

	public void holdSeatAtomic(List<Long> seatIds, Long gameId, String value) {
		defaultRedisScript.setLocation(new ClassPathResource("lua/holdSeat.lua"));

		List<String> keys = seatIds.stream().map(id -> createKey(id, gameId)).toList();
		Long execute = redisTemplate.execute(defaultRedisScript, keys, value, SEAT_HOLD_TTL_STRING);

		if(execute == null || execute.equals(0L)) {
			throw new ServerException(TICKET_ALREADY_RESERVED);
		}
	}

	public void releaseSeat(List<Long> seatIds, Long gameId) {
		for (Long seatId : seatIds) {
			String key = createKey(seatId, gameId);
			redisTemplate.delete(key);
		}
	}

	public void releaseSeatAtomic(List<Long> seatIds, Long gameId) {
		defaultRedisScript.setLocation(new ClassPathResource("lua/releaseSeat.lua"));

		List<String> keys = seatIds.stream().map(id -> createKey(id, gameId)).toList();
		redisTemplate.execute(defaultRedisScript, keys);
	}

	public void checkHeldSeat(List<Long> seatIds, Long gameId, String value) {
		for (Long seatId : seatIds) {
			String key = createKey(seatId, gameId);
			String v = redisTemplate.opsForValue().get(key);

			if (v == null || !v.equals(value)) {
				throw new ServerException(TICKET_ALREADY_RESERVED);
			}
		}
	}

	public void checkHeldSeatAtomic(List<Long> seatIds, Long gameId, String value) {
		defaultRedisScript.setLocation(new ClassPathResource("lua/checkHeldSeat.lua"));

		List<String> keys = seatIds.stream().map(id -> createKey(id, gameId)).toList();
		Long isHeld = redisTemplate.execute(defaultRedisScript, keys, value);

		if (isHeld == null || isHeld.equals(0L)) {
			throw new ServerException(TICKET_ALREADY_RESERVED);
		}

	}

	public String createKey(Long seatId, Long gameId){
		return String.format("game:%d:seat:%d", gameId, seatId);
	}
}
