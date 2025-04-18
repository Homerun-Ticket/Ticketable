package com.example.ticketable.domain.game.service;

import com.example.ticketable.common.entity.Auth;
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
import static com.example.ticketable.common.exception.ErrorCode.USER_ACCESS_DENIED;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import com.example.ticketable.domain.ticket.service.TicketService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        try {
            Stadium stadium = stadiumService.getStadium(request.getStadiumId());
            Game game = gameRepository.save(Game.builder()
                    .stadium(stadium)
                    .away(request.getAway())
                    .home(request.getHome())
                    .type(request.getType())
                    .point(request.getPoint())
                    .imagePath(imagePath)
                    .ticketingStartTime((LocalDateTime.now().plusDays(7)))
                    .startTime(request.getStartTime())
                    .build()
            );
            return GameCreateResponse.of(game);
        } catch (ServerException e) {
            imageService.deleteFile(imagePath); // 이미지 삭제 로직
            throw new ServerException(ErrorCode.GAME_SAVE_FAILED);
        }
    }

    public List<GameGetResponse> getGamesV0(String team, LocalDateTime date) {
        List<Game> games;

        if (team != null && date != null) {
            LocalDateTime[] range = getDayRange(date);
            games = gameRepository.findByHomeAndStartTimeBetween(team, range[0], range[1]);
        } else if (team != null) {
            games = gameRepository.findByHome(team);
        } else if (date != null) {
            LocalDateTime[] range = getDayRange(date);
            games = gameRepository.findByStartTimeBetween(range[0], range[1]);
        } else {
            games = gameRepository.findAll();
        }

        return games.stream()
                .map(GameGetResponse::of)
                .collect(Collectors.toList());
    }

    public List<GameGetResponse> getGamesV1(String team, LocalDateTime date) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (date != null) {
            LocalDateTime[] range = getDayRange(date);
            start = range[0];
            end = range[1];
        }
        List<Game> games = gameRepository.findGamesV1(team, start, end);

        return games.stream()
                .map(GameGetResponse::of)
                .collect(Collectors.toList());
    }

    public List<GameGetResponse> getGamesV2(String team, LocalDateTime date) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (date != null) {
            LocalDateTime[] range = getDayRange(date);
            start = range[0];
            end = range[1];
        }

        List<Game> games = gameRepository.findGamesV2(team, start, end);

        return games.stream()
                .map(GameGetResponse::of)
                .collect(Collectors.toList());
    }

    public List<GameGetResponse> getGamesV3(String team, LocalDateTime date) {
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

    // 쿼라 병합 전 버전
    public StadiumGetResponse getStadiumAndSectionSeatCountsV0(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> totalSeats = gameRepository.findTotalSeatsCountInSectionTypeByGameId(gameId);
        Map<String, Long> totalMap = totalSeats.stream()
                .collect(Collectors.toMap(SectionTypeSeatCountResponse::getSectionType, SectionTypeSeatCountResponse::getSeatCount));
        List<SectionTypeSeatCountResponse> bookedSeats = gameRepository.findBookedSeatsCountInSectionTypeByGameId(gameId);
        Map<String, Long> bookedMap = bookedSeats.stream()
                .collect(Collectors.toMap(SectionTypeSeatCountResponse::getSectionType, SectionTypeSeatCountResponse::getSeatCount));

        List<SectionTypeSeatCountResponse> result = totalMap.entrySet().stream()
                .map(entry -> {
                    String type = entry.getKey();
                    Long total = entry.getValue();
                    Long booked = bookedMap.getOrDefault(type, 0L);
                    return new SectionTypeSeatCountResponse(type, total - booked);
                })
                .collect(Collectors.toList());

        return StadiumGetResponse.of(stadium, result);
    }

    // JQPL 버전
    public StadiumGetResponse getStadiumAndSectionSeatCountsV1(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> sectionBookedSeatCounts = gameRepository.findUnBookedSeatsCountInSectionTypeByGameIdV1(gameId);


        return StadiumGetResponse.of(stadium, sectionBookedSeatCounts);
    }

    // Native 쿼리 버전
    public StadiumGetResponse getStadiumAndSectionSeatCountsV2(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> sectionBookedSeatCounts = gameCacheService.getSectionSeatCountsCached(gameId);
        return StadiumGetResponse.of(stadium, sectionBookedSeatCounts);
    }

    // QueryDSL 버전
    public StadiumGetResponse getStadiumAndSectionSeatCountsV3(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> sectionBookedSeatCounts = gameRepository.findUnBookedSeatsCountInSectionTypeByGameIdV3(gameId);

        return StadiumGetResponse.of(stadium, sectionBookedSeatCounts);
    }


    public List<SectionSeatCountResponse> getAvailableSeatsBySectionType(Long gameId, String type) {
        return gameRepository.findSectionSeatCountsBySectionId(gameId, type);
    }

    public List<SeatGetResponse> getSeatInfoBySection(Long sectionId, Long gameId) {
        return gameRepository.findSeatsWithBookingStatusBySectionIdAndGameId(sectionId ,gameId);
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
    }

    // 날짜 계산 메서드
    private LocalDateTime[] getDayRange(LocalDateTime dateTime) {
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[] { startOfDay, endOfDay };
    }




}
