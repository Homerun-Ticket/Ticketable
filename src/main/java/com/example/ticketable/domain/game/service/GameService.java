package com.example.ticketable.domain.game.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
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

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    private final StadiumService stadiumService;

    @Transactional
    public GameCreateResponse createGame(GameCreateRequest request) {
        Stadium stadium = stadiumService.getStadium(request.getStadiumId());
        Game game = gameRepository.save(Game.builder()
                .stadium(stadium)
                .away(request.getAway())
                .home(request.getHome())
                .type(request.getType())
                .point(request.getPoint())
                .imagePath(request.getImagePath())
                .startTime(request.getStartTime())
                .build()
        );
        return GameCreateResponse.of(game);
    }

    public List<GameGetResponse> getGames(String team, LocalDateTime date) {
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

    public StadiumGetResponse getStadiumAndSectionSeatCounts(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> sectionBookedSeatCounts = gameRepository.findUnBookedSeatsCountInSectionTypeByGameId(gameId);

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
    }

    // 날짜 계산 메서드
    private LocalDateTime[] getDayRange(LocalDateTime dateTime) {
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[] { startOfDay, endOfDay };
    }

	public Game getGameEntity(Long gameId) {
		return gameRepository.findById(gameId).orElseThrow(()->new ServerException(USER_ACCESS_DENIED));
	}
}
