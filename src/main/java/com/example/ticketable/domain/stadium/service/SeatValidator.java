package com.example.ticketable.domain.stadium.service;

import static com.example.ticketable.common.exception.ErrorCode.SEAT_NOT_FOUND;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.repository.GameRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatValidator {
	private final GameRepository gameRepository;
	//좌석이 경기에 포함되어있는지 판단
	public void validateSeatsBelongToGame(Long gameId, List<Long> seatIds) {
		List<Long> validSeatIds = gameRepository.findValidSeatIdsByGameId(gameId, seatIds);
		if (validSeatIds.size() != seatIds.size()) {
			throw new ServerException(SEAT_NOT_FOUND);
		}
	}
}
