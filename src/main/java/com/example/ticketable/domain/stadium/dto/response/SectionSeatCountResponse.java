package com.example.ticketable.domain.stadium.dto.response;


import lombok.Getter;

@Getter
public class SectionSeatCountResponse {
    private final String sectionCode;
    private final Long seatCount;

    public SectionSeatCountResponse(String sectionCode, Long seatCount) {
        this.sectionCode = sectionCode;
        this.seatCount = seatCount;
    }
}
