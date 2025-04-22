package com.example.ticketable.domain.game.service;

import com.example.ticketable.domain.game.dto.response.GameGetResponse;
import com.example.ticketable.domain.game.repository.GameRepository;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.StadiumGetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
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
            StadiumGetResponse response = gameService.getSeatCountsByType(gameId);

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

    @Test
    void getStadiumAndSectionSeatCounts2() {
        // given
        Long gameId = 1L;
        long result [] = new long[10];
        String type = "외야석";
        long sum = 0;
        for (int i = 0; i<10; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // when
           List<SectionSeatCountResponse> response = gameService.getSeatCountsBySection(gameId, type);

            // then
            stopWatch.stop();
            result[i] = stopWatch.getTotalTimeMillis();
            assertNotEquals(response.size(), 0);
        }

        for (int i = 0; i<10; i++) {
            System.out.println("V2time: " + result[i] +"ms");
        }

        for (long time : result) sum += time;
        System.out.println("v2 버전 평균 실행 시간: " + (sum / result.length) + "ms");

    }

    @Test
    void getGamesPerformanceTestByConditions() {
        LocalDateTime date = LocalDateTime.of(2025, 4, 20, 0, 0);
        String team = "롯데";

        record Condition(String label, String team, LocalDateTime date) {}
        List<Condition> conditions = List.of(
                new Condition("ALL", null, null),
                new Condition("TEAM_ONLY", team, null),
                new Condition("DATE_ONLY", null, date),
                new Condition("TEAM+DATE", team, date)
        );
        System.out.println("▶ 테스트 기준 시간: " + LocalDateTime.now());
        gameRepository.findAll().forEach(game ->
                System.out.printf("▶ gameId: %d, 티켓 시작: %s, 경기 시작: %s%n",
                        game.getId(),
                        game.getTicketingStartTime(),
                        game.getStartTime()
                )
        );
        for (Condition condition : conditions) {
            double[] v3Time = new double[10];

            for (int i = 0; i < 10; i++) {
                long start = System.nanoTime();
                List<GameGetResponse> v3 = gameService.getGames(condition.team(), condition.date());
                long end = System.nanoTime();

                v3Time[i] = (end - start) / 1_000_000.0; // ms 단위 소수점 포함
                assertFalse(v3.isEmpty(), condition.label() + " - V3 결과 없음");
            }

            double v3Avg = Arrays.stream(v3Time).average().orElse(0.0);

            for (int i = 0; i < 10; i++) {
                System.out.printf("V3time: %.2fms%n", v3Time[i]);
            }
            System.out.printf("🔍 [%s] 평균 실행 시간\n", condition.label());
            System.out.printf("✅ V3: %.2fms\n\n", v3Avg);
        }
    }
}