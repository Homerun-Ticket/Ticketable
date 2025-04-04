package com.example.ticketable.domain.stadium.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SeatUpdateRequest {

    private boolean isBlind;

    public SeatUpdateRequest(boolean isBlind) {
        this.isBlind = isBlind;
    }
}
