package com.example.ticketable.domain.stadium.dto.request;

import com.example.ticketable.domain.stadium.entity.Section;
import lombok.Getter;

@Getter
public class SectionCreateRequest {
    private String type;

    private String code;

    private Integer extraCharge;

    public SectionCreateRequest(String type, String code, Integer extraCharge) {
        this.type = type;
        this.code = code;
        this.extraCharge = extraCharge;
    }

    public static SectionCreateRequest of(Section section) {
        return new SectionCreateRequest(
                section.getType(),
                section.getCode(),
                section.getExtraCharge()
        );
    }
}
