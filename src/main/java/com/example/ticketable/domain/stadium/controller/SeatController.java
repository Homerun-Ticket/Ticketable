package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stadiums/{stadiumId}/sections/{sectionId}/seats")
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<List<SeatCreateResponse>> createSeats(
        @PathVariable Long sectionId,
        @RequestBody SeatCreateRequest request
    ) {
        return ResponseEntity.ok(seatService.createSeats(sectionId, request));
    }

    @GetMapping
    public ResponseEntity<List<SeatGetResponse>> getSeats(
            @PathVariable Long sectionId
    ) {
        return ResponseEntity.ok(seatService.getSeats(sectionId));
    }

}
