package com.example.ticketable.domain.game.util;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GameCacheHelper {
    private final GameRepository gameRepository;

    // 캐시 무효화 전략
    public boolean isEvictStrategy(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(game.getTicketingStartTime().plusMinutes(30));
    }
}
