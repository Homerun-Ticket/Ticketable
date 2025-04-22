package com.example.ticketable.domain.stadium.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SeatUpdateRequest {

    private boolean isBlind;

    public SeatUpdateRequest(boolean isBlind) {
        this.isBlind = isBlind;
    }
}
