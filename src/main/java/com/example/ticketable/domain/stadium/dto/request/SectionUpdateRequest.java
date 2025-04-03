package com.example.ticketable.domain.stadium.dto.request;

import com.example.ticketable.domain.stadium.entity.Section;
import lombok.Getter;

@Getter
public class SectionUpdateRequest {
    private String type;

    private String code;

    private Integer extraCharge;

    public SectionUpdateRequest(String type, String code, Integer extraCharge) {
        this.type = type;
        this.code = code;
        this.extraCharge = extraCharge;
    }

    public static SectionUpdateRequest of(Section section) {
        return new SectionUpdateRequest(
                section.getType(),
                section.getCode(),
                section.getExtraCharge()
        );
    }
}
