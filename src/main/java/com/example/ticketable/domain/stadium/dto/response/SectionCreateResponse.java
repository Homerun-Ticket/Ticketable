package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import lombok.Getter;

@Getter
public class SectionCreateResponse {
    private final Long id;

    private final String type;

    private final String code;

    private final Integer extraCharge;


    public SectionCreateResponse(Long id, String type, String code, Integer extraCharge) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.extraCharge = extraCharge;
    }

    public static SectionCreateResponse of(Section section) {
        return new SectionCreateResponse(
                section.getId(),
                section.getType(),
                section.getCode(),
                section.getExtraCharge()
        );
    }
}
