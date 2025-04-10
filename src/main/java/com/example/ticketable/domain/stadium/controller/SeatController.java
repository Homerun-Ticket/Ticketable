package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.domain.stadium.dto.request.SeatCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatHoldRequest;
import com.example.ticketable.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SeatCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.ticketable.domain.stadium.service.SeatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

    // 좌석 선점
    @PostMapping("/v1/seats/hold")
    public ResponseEntity<String> holdSeat(
        @AuthenticationPrincipal Auth auth,
        @RequestBody SeatHoldRequest seatHoldRequest
    ) {
        seatService.holdSeat(auth, seatHoldRequest);
        log.debug("사용자 : {} , 좌석 : {} 선점완료", auth.getId(), seatHoldRequest.getSeatIds());

        return ResponseEntity.ok("모든 좌석 선점 성공, 15분안에 결제를 완료해주세요");
    }
}
