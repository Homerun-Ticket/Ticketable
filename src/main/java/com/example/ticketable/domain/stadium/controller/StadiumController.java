package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.StadiumCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.StadiumUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.StadiumCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumUpdateResponse;
import com.example.ticketable.domain.stadium.service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StadiumController {
    private final StadiumService stadiumService;

    @PostMapping("/v1/stadiums")
    public ResponseEntity<StadiumCreateResponse> createStadium(
            @Valid @RequestPart(value = "json") StadiumCreateRequest request,
            @RequestPart(value = "image") MultipartFile file
    ) {
        return ResponseEntity.ok(stadiumService.createStadium(request, file));
    }

    @PutMapping("/v1/stadiums/{stadiumId}")
    public ResponseEntity<StadiumUpdateResponse> updateStadium(
            @PathVariable Long stadiumId,
            @RequestBody StadiumUpdateRequest requset
    ) {
        return ResponseEntity.ok(stadiumService.updateStadium(stadiumId, requset));
    }

    @DeleteMapping("/v1/stadiums/{stadiumId}")
    public ResponseEntity<Void> deleteStadium(@PathVariable Long stadiumId) {
        stadiumService.deleteStadium(stadiumId);
        return ResponseEntity.ok().build();
    }
}
