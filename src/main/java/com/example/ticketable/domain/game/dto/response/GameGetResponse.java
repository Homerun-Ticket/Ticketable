package com.example.ticketable.domain.game.dto.response;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.enums.GameType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameGetResponse {
    private final Long id;

    private final String away;

    private final String home;

    private final GameType type;

    private final Integer point;

    private final String imagePath;

    private final LocalDateTime startTime;


    public GameGetResponse(Long id, String away, String home, GameType type, Integer point, String imagePath, LocalDateTime startTime) {
        this.id = id;
        this.away = away;
        this.home = home;
        this.type = type;
        this.point = point;
        this.imagePath = imagePath;
        this.startTime = startTime;
    }

    public static GameGetResponse of(Game game) {
        return new GameGetResponse(
                game.getId(),
                game.getAway(),
                game.getHome(),
                game.getType(),
                game.getPoint(),
                game.getImagePath(),
                game.getStartTime()
        );
    }
}
