package com.example.ticketable.domain.stadium.dto.response;


import lombok.Getter;

@Getter
public class SectionTypeSeatCountResponse {
    private final String sectionType;
    private final Long seatCount;

    public SectionTypeSeatCountResponse(String sectionType, Long seatCount) {
        this.sectionType = sectionType;
        this.seatCount = seatCount;
    }
}
