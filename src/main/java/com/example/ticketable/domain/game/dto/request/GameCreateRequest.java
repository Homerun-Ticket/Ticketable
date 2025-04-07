package com.example.ticketable.domain.game.dto.request;

import com.example.ticketable.domain.game.enums.GameType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameCreateRequest {
    private Long stadiumId;

    private String away;

    private String home;

    private GameType type;

    private Integer point;

    private String imagePath;

    private LocalDateTime startTime;

    public GameCreateRequest(Long stadiumId, String away, String home, GameType type, Integer point, String imagePath, LocalDateTime startTime) {
        this.stadiumId = stadiumId;
        this.away = away;
        this.home = home;
        this.type = type;
        this.point = point;
        this.imagePath = imagePath;
        this.startTime = startTime;
    }
}
