package com.example.ticketable.common.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class LuaScriptConfig {

	/**
	 * Redis Lua 스크립트: 좌석 점유 시도 스크립트 반환
	 *
	 * <p>스크립트 동작 결과:</p>
	 * <ul>
	 *     <li>1L: 모든 좌석 점유 성공</li>
	 *     <li>0L: 점유 실패 (이미 점유된 좌석 존재)</li>
	 * </ul>
	 *
	 * @return holdSeat.lua에 정의된 좌석 점유 Redis Lua 스크립트
	 */
	@Bean
	public DefaultRedisScript<Long> holdSeatRedisScript(){
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setLocation(new ClassPathResource("lua/holdSeat.lua"));
		script.setResultType(Long.class);
		return script;
	}

	/**
	 * Redis Lua 스크립트: 좌석 점유 해제 스크립트 반환
	 *
	 * <p>스크립트 동작:</p>
	 * <ul>
	 *     <li>입력으로 받은 모든 좌석 키를 삭제 (redis.call('del', key))</li>
	 *     <li>항상 1L 반환</li>
	 * </ul>
	 *
	 * @return releaseSeat.lua에 정의된 좌석 해제 Redis Lua 스크립트
	 */
	@Bean
	public DefaultRedisScript<Long> releaseSeatRedisScript(){
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setLocation(new ClassPathResource("lua/releaseSeat.lua"));
		script.setResultType(Long.class);
		return script;
	}

	/**
	 * Redis Lua 스크립트: 좌석 점유 확인 스크립트 반환
	 *
	 * <p>스크립트 동작:</p>
	 * <ul>
	 *     <li>입력으로 받은 모든 좌석 키의 값을 조회</li>
	 *     <li>모든 값이 ARGV[1]로 전달된 사용자 ID(value)와 일치하면 1L 반환</li>
	 *     <li>하나라도 불일치하는 경우 즉시 0L 반환</li>
	 * </ul>
	 *
	 * <p>예매 전에 좌석을 점유한 사용자인지 검증할 때 사용됨</p>
	 *
	 * @return checkHeldSeat.lua에 정의된 좌석 점유 확인 Redis Lua 스크립트
	 */
	@Bean
	public DefaultRedisScript<Long> checkHeldRedisScript(){
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setLocation(new ClassPathResource("lua/checkHeldSeat.lua"));
		script.setResultType(Long.class);
		return script;
	}

	/**
	 * Redis Lua 스크립트를 통해 대기열(Waiting Queue)의 사용자들을 입장 큐(Proceed Queue)로 이동시키는 스크립트 Bean.
	 *
	 * <p>스크립트 동작 개요:
	 * <ul>
	 *     <li>현재 Proceed Queue의 사용자 수를 조회 (ZCARD)</li>
	 *     <li>남은 수용량(CAPACITY - 현재 인원)만큼 Waiting Queue에서 사용자 pop</li>
	 *     <li>pop된 사용자들을 Proceed Queue에 ZADD로 추가하고, Waiting Queue에서 ZREM</li>
	 *     <li>모든 작업은 원자적으로 처리됨</li>
	 * </ul>
	 *
	 * @return DefaultRedisScript - Lua 스크립트 실행을 위한 RedisScript Bean
	 */
	@Bean
	public DefaultRedisScript<Long> moveWaitingToProceedScript(){
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setResultType(Long.class);
		script.setLocation(new ClassPathResource("lua/moveWaitingToProceed.lua"));
		return script;
	}

	@Bean
	public DefaultRedisScript<Long> moveWaitingToProceedScriptV2(){
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setResultType(Long.class);
		script.setLocation(new ClassPathResource("lua/moveWaitingToProceedV2.lua"));
		return script;
	}
}
