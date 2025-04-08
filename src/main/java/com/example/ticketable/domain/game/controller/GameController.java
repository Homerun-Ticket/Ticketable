package com.example.ticketable.domain.game.controller;

import com.example.ticketable.domain.game.dto.request.GameCreateRequest;
import com.example.ticketable.domain.game.dto.request.GameUpdateRequest;
import com.example.ticketable.domain.game.dto.response.GameCreateResponse;
import com.example.ticketable.domain.game.dto.response.GameGetResponse;
import com.example.ticketable.domain.game.dto.response.GameUpdateResponse;
import com.example.ticketable.domain.game.service.GameService;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    @PostMapping("/v1/games")
    public ResponseEntity<GameCreateResponse> createGame(
            @Valid @RequestPart(value = "json") GameCreateRequest request,
            @RequestPart(value = "image") MultipartFile file
            ) {
        return ResponseEntity.ok(gameService.createGame(request, file));
    }

    @GetMapping("/v1/games")
    public ResponseEntity<List<GameGetResponse>> getGames(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
            ) {
        return ResponseEntity.ok(gameService.getGames(team, date));
    }

    @GetMapping("/v1/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCounts(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getStadiumAndSectionSeatCounts(gameId));
    }

    @GetMapping("/v1/games/{gameId}/sectionTypes")
    public ResponseEntity<List<SectionSeatCountResponse>> getAvailableSeatsBySectionType(
            @PathVariable Long gameId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(gameService.getAvailableSeatsBySectionType(gameId, type));
    }

    @GetMapping("/v1/games/{gameId}/sections/{sectionId}")
    public ResponseEntity<List<SeatGetResponse>> getSeatInfoBySection(
            @PathVariable Long gameId,
            @PathVariable Long sectionId
    ) {
        return ResponseEntity.ok(gameService.getSeatInfoBySection(sectionId, gameId));
    }

    @PutMapping("/v1/games/{gameId}")
    public ResponseEntity<GameUpdateResponse> updateGame(
            @PathVariable Long gameId,
            @RequestBody GameUpdateRequest request
    ) {
        return ResponseEntity.ok(gameService.updateGame(gameId, request));
    }

    @DeleteMapping("/v1/games/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGames(gameId);
        return ResponseEntity.ok().build();
    }


}
