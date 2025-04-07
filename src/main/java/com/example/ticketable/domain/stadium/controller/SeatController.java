package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.ticketable.domain.stadium.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/v1/stadiums/{stadiumId}/sections/{sectionId}/seats")
    public ResponseEntity<List<SeatCreateResponse>> createSeats(
            @PathVariable Long stadiumId,
            @PathVariable Long sectionId,
            @RequestBody SeatCreateRequest request
    ) {
        return ResponseEntity.ok(seatService.createSeats(stadiumId, sectionId, request));
    }

    @PutMapping("/v1/seats/{seatId}")
    public ResponseEntity<SeatUpdateResponse> updateSeat(
            @PathVariable Long seatId,
            @RequestBody SeatUpdateRequest request
    ) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, request));
    }

    @DeleteMapping("/v1/seats/{seatId}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long seatId
    ) {
        seatService.delete(seatId);
        return ResponseEntity.ok().build();
    }
}
