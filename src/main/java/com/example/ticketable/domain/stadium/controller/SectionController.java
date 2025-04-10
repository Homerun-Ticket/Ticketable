package com.example.ticketable.domain.stadium.controller;

import com.example.ticketable.domain.stadium.dto.request.SectionCreateRequest;
import com.example.ticketable.domain.stadium.dto.request.SectionUpdateRequest;
import com.example.ticketable.domain.stadium.dto.response.SectionCreateResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionUpdateResponse;
import com.example.ticketable.domain.stadium.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/v1/stadiums/{stadiumId}/sections")
    public ResponseEntity<SectionCreateResponse> createSection(
            @PathVariable Long stadiumId,
            @RequestBody SectionCreateRequest request
    ) {
        return ResponseEntity.ok(sectionService.createSection(stadiumId, request));
    }

    @PutMapping("/v1/sections/{sectionId}")
    public ResponseEntity<SectionUpdateResponse> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionUpdateRequest request
    ) {
        return ResponseEntity.ok(sectionService.updateSection(sectionId, request));
    }


    @DeleteMapping("/v1/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long sectionId
    ) {
        sectionService.delete(sectionId);
        return ResponseEntity.ok().build();
    }
}
