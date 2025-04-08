package com.example.ticketable.common.config;

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
	public DefaultRedisScript<Long> realizeSeatRedisScript(){
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
}
