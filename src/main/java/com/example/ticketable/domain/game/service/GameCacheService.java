package com.example.ticketable.domain.game.service;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.game.util.GameCacheHelper;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.entity.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameCacheService {
    private final GameRepository gameRepository;
    private final CacheManager cacheManager;
    private final GameCacheHelper gameCacheHelper;


    @Cacheable(value = "seatCountsBySectionType", key = "#gameId")
    public List<SectionTypeSeatCountResponse> getSeatCountsByTypeCached(Long gameId) {
        log.info("💡 캐시 미적중! DB에서 seat count 조회 - gameId: {}", gameId);
        return gameRepository.findSeatCountsByType(gameId);
    }

    @Cacheable(value = "seatCountsBySectionCode", key = "T(String).format('%s:%s', #gameId, #type)")
    public List<SectionSeatCountResponse> getSeatCountsBySectionCached(Long gameId, String type) {
        log.info("💡 캐시 미적중! DB에서 seat count 조회 - gameId: {} - type {}", gameId, type);
        return gameRepository.findSeatCountsBySection(gameId, type);
    }

    @Cacheable(
            value = "gamesByCondition",  // 캐시 이름
            key = "T(String).format('%s:%s', #team == null ? 'all' : #team, #start != null ? #start.toLocalDate() : 'all')"  // 조건 조합 key
    )
    public List<Game> getGamesCached(String team, LocalDateTime start, LocalDateTime end) {
        log.info("💡 캐시 미적중! DB에서 game 조회");
        return gameRepository.findGames(team, start, end);
    }

    @Cacheable(
            value = "seat",  // 캐시 이름
            key = "T(String).format('%s:%s', #gameId, #sectionId)"  // 조건 조합 key
    )
    public List<SeatGetResponse> getSeatsCached(Long sectionId, Long gameId) {
        log.info("💡 캐시 미적중! DB에서 seat 조회");
        return gameRepository.findSeatsInfo(sectionId, gameId).stream()
                .map(row -> new SeatGetResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (Boolean) row[2],
                        ((Number) row[3]).intValue() == 1
                ))
                .toList();
    }


    public void handleAfterTicketChange(Long gameId) {
        Cache cache = cacheManager.getCache("seatCountsBySectionType");

        if (gameCacheHelper.isEvictStrategy(gameId+":")) {
            cache.evict(gameId);
            log.info("캐시 삭제");
        } else {
            List<SectionTypeSeatCountResponse> updated =
                    gameRepository.findSeatCountsByType(gameId);
            cache.put(gameId, updated);
            log.info("캐시 갱신");
        }
    }

    public void handleAfterTicketChangeByType(Long gameId, String type) {
        Cache cache = cacheManager.getCache("seatCountsBySectionCode");
        String key = String.format("%s:%s", gameId, type);
        if (gameCacheHelper.isEvictStrategy(key)) {
            cache.evict(key);
            log.info("캐시 삭제");
        } else {
            List<SectionSeatCountResponse> updated =
                    gameRepository.findSeatCountsBySection(gameId, type);
            cache.put(key, updated);
            log.info("캐시 갱신");
        }
    }

    public void handleAfterTicketChangeBySeat(Long gameId, Long sectionId) {
        Cache cache = cacheManager.getCache("seat");
        String key = String.format("%s:%s", gameId, sectionId);
        if (gameCacheHelper.isEvictStrategy(key)) {
            cache.evict(key);
            log.info("캐시 삭제");
        } else {
            List<SeatGetResponse> updated =
                gameRepository.findSeatsInfo(sectionId, gameId).stream()
                    .map(row -> new SeatGetResponse(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (Boolean) row[2],
                            ((Number) row[3]).intValue() == 1
                    ))
                    .toList();
            cache.put(key,updated);
            log.info("캐시 갱신");
        }
    }

    public void handleAfterTicketChangeAll(Long gameId, Seat seat) {
        String type = seat.getSection().getType();
        handleAfterTicketChange(gameId);             // sectionType 캐시 처리
        handleAfterTicketChangeByType(gameId, type); // sectionCode 캐시 처리
        handleAfterTicketChangeBySeat(gameId, seat.getSection().getId());
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void clearAllGameCaches() {
        Cache cache = cacheManager.getCache("gamesByCondition");
        if (cache != null) {
            cache.clear();
            log.info("🧹 gamesByCondition 캐시 전체 삭제 완료 (자정 정기 삭제)");
        }
    }
}
