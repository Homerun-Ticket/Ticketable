package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Seat;
import lombok.Getter;

@Getter
public class SeatGetResponse {
    private final Long id;

    private final String position;

    private final Boolean isBlind;

    private final Boolean isBooked;

    public SeatGetResponse(Long id, String position, Boolean isBlind, Boolean isBooked) {
        this.id = id;
        this.position = position;
        this.isBlind = isBlind;
        this.isBooked = isBooked;
    }

    public static SeatGetResponse of(Seat seat, boolean isBooked) {
        return new SeatGetResponse(
                seat.getId(),
                seat.getPosition(),
                seat.isBlind(),
                isBooked
        );
    }
}
