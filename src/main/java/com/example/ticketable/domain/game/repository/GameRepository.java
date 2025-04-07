package com.example.ticketable.domain.game.repository;


import com.example.ticketable.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByHomeAndStartTimeBetween(String team, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Game> findByHome(String home);

    List<Game> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
