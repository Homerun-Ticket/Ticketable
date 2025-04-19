package com.example.ticketable.domain.game.controller;

import com.example.ticketable.common.entity.Auth;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/v0/games")
    public ResponseEntity<List<GameGetResponse>> getGamesV0(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
    ) {
        return ResponseEntity.ok(gameService.getGamesV0(team, date));
    }

    @GetMapping("/v1/games")
    public ResponseEntity<List<GameGetResponse>> getGamesV1(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
    ) {
        return ResponseEntity.ok(gameService.getGamesV1(team, date));
    }

    @GetMapping("/v2/games")
    public ResponseEntity<List<GameGetResponse>> getGamesV2(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
    ) {
        return ResponseEntity.ok(gameService.getGamesV2(team, date));
    }

    @GetMapping("/v3/games")
    public ResponseEntity<List<GameGetResponse>> getGamesV3(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
    ) {
        return ResponseEntity.ok(gameService.getGamesV3(team, date));
    }

    @GetMapping("/v0/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCountsV0(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getStadiumAndSectionSeatCountsV0(gameId));
    }

    @GetMapping("/v1/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCounts(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getStadiumAndSectionSeatCountsV1(gameId));
    }

    @GetMapping("/v2/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCountsV2(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getStadiumAndSectionSeatCountsV2(gameId));
    }

    @GetMapping("/v3/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCountsV3(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getStadiumAndSectionSeatCountsV3(gameId));
    }


    @GetMapping("/v1/games/{gameId}/sectionTypes")
    public ResponseEntity<List<SectionSeatCountResponse>> getAvailableSeatsBySectionType(
            @PathVariable Long gameId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(gameService.getAvailableSeatsBySectionTypeV1(gameId, type));
    }

    @GetMapping("/v2/games/{gameId}/sectionTypes")
    public ResponseEntity<List<SectionSeatCountResponse>> getAvailableSeatsBySectionTypeV2(
            @PathVariable Long gameId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(gameService.getAvailableSeatsBySectionTypeV2(gameId, type));
    }

    @GetMapping("/v3/games/{gameId}/sectionTypes")
    public ResponseEntity<List<SectionSeatCountResponse>> getAvailableSeatsBySectionTypeV3(
            @PathVariable Long gameId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(gameService.getAvailableSeatsBySectionTypeV3(gameId, type));
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
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long gameId
    ) {
        gameService.deleteGames(gameId);
        return ResponseEntity.ok().build();
    }


}
