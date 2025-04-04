package com.example.ticketable.domain.game.service;

import static com.example.ticketable.common.exception.ErrorCode.USER_ACCESS_DENIED;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
	private final GameRepository gameRepository;

	public Game getGameEntity(Long gameId) {
		return gameRepository.findById(gameId).orElseThrow(()->new ServerException(USER_ACCESS_DENIED));
	}
}
