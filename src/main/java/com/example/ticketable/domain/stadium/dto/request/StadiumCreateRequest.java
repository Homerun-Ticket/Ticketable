package com.example.ticketable.domain.stadium.dto.request;

import lombok.Getter;

@Getter
public class StadiumCreateRequest {
    private String name;

    private String location;

    public StadiumCreateRequest(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
