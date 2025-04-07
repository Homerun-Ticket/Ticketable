package com.example.ticketable.domain.game.dto.request;

import com.example.ticketable.domain.game.enums.GameType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameUpdateRequest {

    private LocalDateTime startTime;

    public GameUpdateRequest(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
