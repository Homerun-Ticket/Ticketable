package com.example.ticketable.domain.game.service;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.game.util.GameCacheHelper;
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
    public List<SectionTypeSeatCountResponse> getSectionSeatCountsCached(Long gameId) {
        log.info("💡 캐시 미적중! DB에서 seat count 조회 - gameId: {}", gameId);
        return gameRepository.findUnBookedSeatsCountInSectionTypeByGameIdV2(gameId);
    }

    @Cacheable(value = "seatCountsBySectionCode", key = "T(String).format('%s:%s', #gameId, #type)")
    public List<SectionSeatCountResponse> getSectionCodeSeatCountsCached(Long gameId, String type) {
        log.info("💡 캐시 미적중! DB에서 seat count 조회 - gameId: {} - type {}", gameId, type);
        return gameRepository.findSectionSeatCountsBySectionIdV2(gameId, type);
    }

    @Cacheable(
            value = "gamesByCondition",  // 캐시 이름
            key = "T(String).format('%s:%s', #team == null ? 'all' : #team, #start != null ? #start.toLocalDate() : 'all')"  // 조건 조합 key
    )
    public List<Game> getGamesCached(String team, LocalDateTime start, LocalDateTime end) {
        log.info("💡 캐시 미적중! DB에서 game 조회");
        return gameRepository.findGamesV3(team, start, end);
    }

    public void handleAfterTicketChange(Long gameId) {
        Cache cache = cacheManager.getCache("seatCountsBySectionType");

        if (gameCacheHelper.isEvictStrategy(gameId)) {
            cache.evict(gameId);
            log.info("캐시 삭제");
        } else {
            List<SectionTypeSeatCountResponse> updated =
                    gameRepository.findUnBookedSeatsCountInSectionTypeByGameIdV2(gameId);
            cache.put(gameId, updated);
            log.info("캐시 갱신");
        }
    }

    public void handleAfterTicketChangeByType(Long gameId, String type) {
        Cache cache = cacheManager.getCache("seatCountsBySectionCode");
        String key = String.format("%s:%s", gameId, type);
        if (gameCacheHelper.isEvictStrategy(gameId)) {
            cache.evict(key);
            log.info("캐시 삭제");
        } else {
            List<SectionSeatCountResponse> updated =
                    gameRepository.findSectionSeatCountsBySectionIdV2(gameId, type);
            cache.put(key, updated);
            log.info("캐시 갱신");
        }
    }

    public void handleAfterTicketChangeAll(Long gameId, Seat seat) {
        String type = seat.getSection().getType();
        handleAfterTicketChange(gameId);             // sectionType 캐시 처리
        handleAfterTicketChangeByType(gameId, type); // sectionCode 캐시 처리
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
