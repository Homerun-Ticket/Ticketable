package com.example.ticketable.domain.game.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.service.ImageService;
import com.example.ticketable.domain.auction.service.AuctionService;
import com.example.ticketable.domain.game.dto.request.GameCreateRequest;
import com.example.ticketable.domain.game.dto.request.GameUpdateRequest;
import com.example.ticketable.domain.game.dto.response.GameCreateResponse;
import com.example.ticketable.domain.game.dto.response.GameGetResponse;
import com.example.ticketable.domain.game.dto.response.GameUpdateResponse;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.stadium.service.StadiumService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import com.example.ticketable.domain.ticket.service.TicketService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    private final StadiumService stadiumService;
    private final TicketService ticketService;
    private final AuctionService auctionService;

    private final ImageService imageService;

    private final GameCacheService gameCacheService;

    private static final String GAME_FOLDER = "game/";

    @Transactional
    public GameCreateResponse createGame(GameCreateRequest request, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileKey = GAME_FOLDER + UUID.randomUUID()+ "_" + originalFilename;
        String imagePath = imageService.saveFile(file, fileKey);
        if (request.getStartTime().minusDays(7).isBefore(LocalDateTime.now())){
            throw new ServerException(ErrorCode.INVALID_TICKETING_START_TIME);
        }
        try {
            Stadium stadium = stadiumService.getStadium(request.getStadiumId());
            Game game = gameRepository.save(Game.builder()
                    .stadium(stadium)
                    .away(request.getAway())
                    .home(request.getHome())
                    .type(request.getType())
                    .point(request.getPoint())
                    .imagePath(imagePath)
                    .ticketingStartTime((request.getStartTime().minusDays(7)))
                    .startTime(request.getStartTime())
                    .build()
            );
            return GameCreateResponse.of(game);
        } catch (ServerException e) {
            imageService.deleteFile(imagePath); // 이미지 삭제 로직
            throw new ServerException(ErrorCode.GAME_SAVE_FAILED);
        }
    }

    public List<GameGetResponse> getGames(String team, LocalDateTime date) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (date != null) {
            LocalDateTime[] range = getDayRange(date);
            start = range[0];
            end = range[1];
        }

        List<Game> games = gameCacheService.getGamesCached(team, start, end);

        return games.stream()
                .map(GameGetResponse::of)
                .collect(Collectors.toList());
    }

    public StadiumGetResponse getSeatCountsByType(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> sectionBookedSeatCounts = gameCacheService.getSeatCountsByTypeCached(gameId);
        return StadiumGetResponse.of(stadium, sectionBookedSeatCounts);
    }

    public List<SectionSeatCountResponse> getSeatCountsBySection(Long gameId, String type) {
        return gameCacheService.getSeatCountsBySectionCached(gameId, type);
    }

    public List<SeatGetResponse> getSeats(Long sectionId, Long gameId) {
        return gameCacheService.getSeatsCached(gameId, sectionId);
    }

    @Transactional
    public GameUpdateResponse updateGame(Long gameId, GameUpdateRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));
        game.updateStartTime(request.getStartTime());
        return GameUpdateResponse.of(game);
    }

    @Transactional
    public void deleteGames(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));
        game.cancel();
        ticketService.deleteAllTicketsByCanceledGame(gameId);
        auctionService.deleteAllAuctionsByCanceledGame(gameId);
        gameCacheService.clearAllGameCaches();
    }

    // 날짜 계산 메서드
    private LocalDateTime[] getDayRange(LocalDateTime dateTime) {
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[] { startOfDay, endOfDay };
    }




}
