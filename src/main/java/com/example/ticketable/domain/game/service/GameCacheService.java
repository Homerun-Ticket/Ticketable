package com.example.ticketable.domain.game.service;

import com.example.ticketable.common.exception.ErrorCode;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.game.util.GameCacheHelper;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
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
}
