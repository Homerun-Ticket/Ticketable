package com.example.ticketable.domain.game.repository;


import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.stadium.dto.response.SeatGetResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryQuery {

    List<Game> findByHomeAndStartTimeBetween(String team, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Game> findByHome(String home);

    List<Game> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT g FROM Game g
    WHERE (:team IS NULL OR g.home = :team)
      AND (:start IS NULL OR g.startTime >= :start)
      AND (:end IS NULL OR g.startTime <= :end)
      AND CURRENT_TIMESTAMP >= g.ticketingStartTime
      AND CURRENT_TIMESTAMP < g.startTime
""")
    List<Game> findGamesV1(
            @Param("team") String team,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
        SELECT * FROM game g
        WHERE (:team IS NULL OR home = :team)
        AND (:start IS NULL OR start_time >= :start)
        AND (:end IS NULL OR start_time <= :end)
        AND (NOW() >= ticketing_start_time)
        AND (NOW() < start_time)
    """, nativeQuery = true)
    List<Game> findGamesV2(
            @Param("team") String team,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT g.stadium From Game g where g.id = :gameId")
    Stadium getStadiumByGameId(@Param("gameId") Long gameId);


//    // 타입별 예약되지 않는 좌석 수 쿼리 통합 버전
//    @Query("""
//    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
//        s.type, COUNT(seat))
//    FROM Seat seat
//    JOIN seat.section s
//    JOIN s.stadium st
//    JOIN Game g ON g.stadium.id = st.id
//    WHERE g.id = :gameId
//      AND seat.id NOT IN (
//        SELECT ts.seat.id
//        FROM TicketSeat ts
//        JOIN ts.ticket t
//        WHERE t.game.id = :gameId
//          AND t.deletedAt IS NULL
//      )
//    GROUP BY s.type
//    """)
//    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV1(
//            @Param("gameId") Long gameId
//    );
//
//
//
//    // 타입별 예약되지 않는 좌석 수 쿼리 통합 버전 & 서브 쿼리 삭제 버전`
//    @Query("""
//    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
//        s.type, COUNT(seat))
//    FROM Seat seat
//    JOIN seat.section s
//    JOIN s.stadium st
//    JOIN Game g ON g.stadium.id = st.id
//    LEFT JOIN TicketSeat ts ON ts.seat.id = seat.id
//    LEFT JOIN Ticket t ON ts.ticket.id = t.id AND t.game.id = :gameId AND t.deletedAt IS NULL
//    WHERE g.id = :gameId
//        AND t.id IS NULL
//    GROUP BY s.type
//    """)
//    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV2(
//            @Param("gameId") Long gameId
//    );

    @Query("""
SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
    s.type,
    SUM(CASE 
        WHEN t.id IS NULL OR t.deletedAt IS NOT NULL THEN 1
        ELSE 0
    END)
)
FROM Seat seat
JOIN seat.section s
JOIN s.stadium st
JOIN Game g ON g.stadium.id = st.id
LEFT JOIN TicketSeat ts ON ts.seat.id = seat.id
LEFT JOIN Ticket t ON ts.ticket.id = t.id AND t.game.id = :gameId
WHERE g.id = :gameId
GROUP BY s.type
""")
    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV1(
            @Param("gameId") Long gameId
    );

    @Query(value = """
SELECT 
    s.type AS section_type,
    COUNT(seat.id) - COUNT(t.id) AS remaining_seats
FROM Seat seat
JOIN Section s ON seat.section_id = s.id
JOIN Stadium st ON s.stadium_id = st.id
JOIN Game g ON g.stadium_id = st.id
LEFT JOIN Ticket_Seat ts ON ts.seat_id = seat.id
LEFT JOIN Ticket t ON ts.ticket_id = t.id AND t.game_id = :gameId AND t.deleted_at IS NULL
WHERE g.id = :gameId
GROUP BY s.type
""", nativeQuery = true)
    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameIdV2(@Param("gameId") Long gameId);



    // 쿼리 통합 전 전체 좌석 수 조회
    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
        s.type, COUNT(seat))
    FROM Seat seat
    JOIN seat.section s
    JOIN s.stadium st
    JOIN Game g ON g.stadium.id = st.id
    WHERE g.id = :gameId
    GROUP BY s.type
    """)
    List<SectionTypeSeatCountResponse> findTotalSeatsCountInSectionTypeByGameId(
            @Param("gameId") Long gameId
    );

    // 쿼리 통합 전 예약된 좌석 수 조회 (구역 타입 기준)
    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
        s.type, COUNT(ts))
    FROM TicketSeat ts
    JOIN ts.seat seat
    JOIN seat.section s
    JOIN ts.ticket t
    WHERE t.game.id = :gameId
      AND t.deletedAt IS NULL
    GROUP BY s.type
    """)
    List<SectionTypeSeatCountResponse> findBookedSeatsCountInSectionTypeByGameId(
            @Param("gameId") Long gameId
    );




    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse(
        s.code , SUM(
        CASE
        WHEN t.id IS NULL OR t.deletedAt IS NOT NULL THEN 1
        ELSE 0
    END))
    FROM Seat seat
    JOIN seat.section s
    JOIN s.stadium st
    JOIN Game g ON g.stadium.id = st.id
    LEFT JOIN TicketSeat ts ON ts.seat.id = seat.id
    LEFT JOIN Ticket t ON ts.ticket.id = t.id AND t.game.id = :gameId
    WHERE g.id = :gameId
      AND s.type = :type
    GROUP BY s.id
    """)
    List<SectionSeatCountResponse> findSectionSeatCountsBySectionIdV1(
            @Param("gameId") Long gameId,
            @Param("type") String type
    );

    @Query(value = """
SELECT 
    s.code AS section_code,
    COUNT(seat.id) - COUNT(t.id) AS remaining_seats
FROM Seat seat
JOIN Section s ON seat.section_id = s.id
JOIN Stadium st ON s.stadium_id = st.id
JOIN Game g ON g.stadium_id = st.id
LEFT JOIN Ticket_Seat ts ON ts.seat_id = seat.id
LEFT JOIN Ticket t ON ts.ticket_id = t.id AND t.game_id = :gameId AND t.deleted_at IS NULL
WHERE g.id = :gameId
 AND s.type = :type
GROUP BY s.id
""", nativeQuery = true)
    List<SectionSeatCountResponse> findSectionSeatCountsBySectionIdV2(
            @Param("gameId") Long gameId,
            @Param("type") String type
    );

    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SeatGetResponse(
        s.id, s.position, s.isBlind,
        CASE 
            WHEN EXISTS (
                SELECT 1 
                FROM TicketSeat ts 
                JOIN ts.ticket t 
                WHERE ts.seat = s 
                AND t.game.id = :gameId 
                AND t.deletedAt IS NULL
            ) 
            THEN true 
            ELSE false 
        END
    )
    FROM Seat s
    WHERE s.section.id = :sectionId
    """)
    List<SeatGetResponse> findSeatsWithBookingStatusBySectionIdAndGameId(
            @Param("sectionId") Long sectionId,
            @Param("gameId") Long gameId
    );


}
