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

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByHomeAndStartTimeBetween(String team, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Game> findByHome(String home);

    List<Game> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT g.stadium From Game g where g.id = :gameId")
    Stadium getStadiumByGameId(@Param("gameId") Long gameId);

    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse(
        s.type, COUNT(seat))
    FROM Seat seat
    JOIN seat.section s
    JOIN s.stadium st
    JOIN Game g ON g.stadium.id = st.id
    WHERE g.id = :gameId
      AND seat.id NOT IN (
        SELECT ts.seat.id
        FROM TicketSeat ts
        JOIN ts.ticket t
        WHERE t.game.id = :gameId
          AND t.deletedAt IS NULL
      )
    GROUP BY s.type
    """)
    List<SectionTypeSeatCountResponse> findUnBookedSeatsCountInSectionTypeByGameId(
            @Param("gameId") Long gameId
    );

    @Query("""
    SELECT new com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse(
        s.code , COUNT(st.id))
    FROM Seat seat
    JOIN seat.section s
    JOIN s.stadium st
    JOIN Game g ON g.stadium.id = st.id
    WHERE g.id = :gameId
      AND s.type = :type
      AND seat.id NOT IN (
        SELECT ts.seat.id
        FROM TicketSeat ts
        JOIN ts.ticket t
        WHERE t.game.id = :gameId
          AND t.deletedAt IS NULL
      )
    GROUP BY s.code
    """)
    List<SectionSeatCountResponse> findSectionSeatCountsBySectionId(
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
