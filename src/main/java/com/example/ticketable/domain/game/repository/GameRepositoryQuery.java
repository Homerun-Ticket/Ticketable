package com.example.ticketable.domain.game.repository;

import com.example.ticketable.domain.game.entity.Game;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepositoryQuery {

    List<Game> findGames(String team, LocalDateTime start, LocalDateTime end);
}
