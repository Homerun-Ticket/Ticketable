package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Stadium;
import lombok.Getter;

@Getter
public class StadiumGetResponse {
    private final Long id;

    private final String name;

    private final String location;

    private final Integer capacity;

    public StadiumGetResponse(Long id, String name, String location, Integer capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public static StadiumGetResponse of(Stadium stadium) {
        return new StadiumGetResponse(
                stadium.getId(),
                stadium.getName(),
                stadium.getLocation(),
                stadium.getCapacity()
        );
    }
}
