package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.StadiumUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumUpdateResponse;
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
        return ResponseEntity.ok(stadiumService.getStadiumDto(stadiumId));
    }

    @PutMapping("{stadiumId}")
    public ResponseEntity<StadiumUpdateResponse> updateStadium(
            @PathVariable Long stadiumId,
            @RequestBody StadiumUpdateRequest requset
    ) {
        return ResponseEntity.ok(stadiumService.updateStadium(stadiumId, requset));
    }

    @DeleteMapping("{stadiumId}")
    public ResponseEntity<Void> deleteStadium(@PathVariable Long stadiumId) {
        stadiumService.deleteStadium(stadiumId);
        return ResponseEntity.ok().build();
    }
}
