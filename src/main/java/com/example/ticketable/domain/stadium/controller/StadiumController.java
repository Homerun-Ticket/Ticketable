package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stadiums")
public class StadiumController {
    private final StadiumService stadiumService;

    @PostMapping
    public ResponseEntity<StadiumCreateResponse> createStadium(@RequestBody StadiumCreateRequest request) {
        return ResponseEntity.ok(stadiumService.createStadium(request));
    }

    @GetMapping("{stadiumId}")
    public ResponseEntity<StadiumGetResponse> getStadium(@PathVariable Long stadiumId) {
        return ResponseEntity.ok(stadiumService.getStadium(stadiumId));
    }
}
