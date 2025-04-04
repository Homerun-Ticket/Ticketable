package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import lombok.Getter;

@Getter
public class SeatCreateResponse {
    private final Long id;

    private final String position;

    private final boolean isBlind;


    public SeatCreateResponse(Long id, String position, boolean isBlind) {
        this.id = id;
        this.position = position;
        this.isBlind = isBlind;
    }

    public static SeatCreateResponse of(Seat seat) {
        return new SeatCreateResponse(
                seat.getId(),
                seat.getPosition(),
                seat.isBlind()
        );
    }
}
