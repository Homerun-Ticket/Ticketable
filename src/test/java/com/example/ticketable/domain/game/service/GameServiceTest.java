package com.example.ticketable.domain.game.service;

import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GameServiceTest {
    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void getStadiumAndSectionSeatCounts() {
        // given
        Long gameId = 1L;
        long result [] = new long[10];
        long sum = 0;
        for (int i = 0; i<10; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // when
            StadiumGetResponse response = gameService.getStadiumAndSectionSeatCountsV2(gameId);

            // then
            stopWatch.stop();
            result[i] = stopWatch.getTotalTimeMillis();
            assertNotNull(response.getId());
        }

        for (int i = 0; i<10; i++) {
            System.out.println("V2time: " + result[i] +"ms");
        }

        for (long time : result) sum += time;
        System.out.println("v2 버전 평균 실행 시간: " + (sum / result.length) + "ms");

    }
}