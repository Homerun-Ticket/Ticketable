package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Seat;
import lombok.Getter;

@Getter
public class SeatUpdateResponse {
    private final Long id;

    private final String position;

    private final boolean isBlind;


    public SeatUpdateResponse(Long id, String position, boolean isBlind) {
        this.id = id;
        this.position = position;
        this.isBlind = isBlind;
    }

    public static SeatUpdateResponse of(Seat seat) {
        return new SeatUpdateResponse(
                seat.getId(),
                seat.getPosition(),
                seat.isBlind()
        );
    }
}
