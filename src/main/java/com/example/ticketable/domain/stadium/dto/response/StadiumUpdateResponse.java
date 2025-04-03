package com.example.ticketable.domain.stadium.dto.response;

import com.example.ticketable.domain.stadium.entity.Stadium;
import lombok.Getter;

@Getter
public class StadiumUpdateResponse {
    private final Long id;

    private final String name;

    private final String location;

    private final Integer capacity;

    private final String imagePath;

    public StadiumUpdateResponse(Long id, String name, String location, Integer capacity, String imagePath) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.imagePath = imagePath;
    }

    public static StadiumUpdateResponse of(Stadium stadium) {
        return new StadiumUpdateResponse(
                stadium.getId(),
                stadium.getName(),
                stadium.getLocation(),
                stadium.getCapacity(),
                stadium.getImagePath()
        );
    }
}
