package com.example.ticketable.domain.game.repository;


import com.example.ticketable.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}
