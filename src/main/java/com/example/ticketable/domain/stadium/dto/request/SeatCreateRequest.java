package com.example.ticketable.domain.stadium.dto.request;

import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import lombok.Getter;

import java.util.List;

@Getter
public class SeatCreateRequest {
    private List<List<String>> colNums;

    private List<List<Boolean>> isBlind;

    public SeatCreateRequest(List<List<String>> colNums, List<List<Boolean>> isBlind) {
        this.colNums = colNums;
        this.isBlind = isBlind;
    }
}
