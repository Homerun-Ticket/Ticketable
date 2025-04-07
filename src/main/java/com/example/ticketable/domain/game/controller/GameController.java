package com.example.ticketable.domain.game.controller;

import com.example.ticketable.domain.game.dto.request.GameCreateRequest;
import com.example.ticketable.domain.game.dto.request.GameUpdateRequest;
import com.example.ticketable.domain.game.dto.response.GameCreateResponse;
import com.example.ticketable.domain.game.dto.response.GameGetResponse;
import com.example.ticketable.domain.game.dto.response.GameUpdateResponse;
import com.example.ticketable.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    @PostMapping("/v1/games")
    public ResponseEntity<GameCreateResponse> createGame(@RequestBody GameCreateRequest request) {
        return ResponseEntity.ok(gameService.createGame(request));
    }

    @GetMapping("/v1/games")
    public ResponseEntity<List<GameGetResponse>> getGames(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
            ) {
        return ResponseEntity.ok(gameService.getGames(team, date));
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
