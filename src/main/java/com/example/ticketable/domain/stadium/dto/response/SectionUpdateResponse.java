package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Section;
import lombok.Getter;

@Getter
public class SectionUpdateResponse {
    private final Long id;

    private final String type;

    private final String code;

    private final Integer extraCharge;


    public SectionUpdateResponse(Long id, String type, String code, Integer extraCharge) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.extraCharge = extraCharge;
    }

    public static SectionUpdateResponse of(Section section) {
        return new SectionUpdateResponse(
                section.getId(),
                section.getType(),
                section.getCode(),
                section.getExtraCharge()
        );
    }
}
