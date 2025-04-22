package com.example.ticketable.domain.game.repository;


import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.ticketable.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.ticketable.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryQuery {


    @Query("SELECT g.stadium From Game g where g.id = :gameId")
    Stadium getStadiumByGameId(@Param("gameId") Long gameId);

    @Query(value = """
SELECT 
    s.type AS section_type,
    COUNT(seat.id) - COUNT(t.id) AS remaining_seats
FROM seat seat
JOIN section s ON seat.section_id = s.id
JOIN stadium st ON s.stadium_id = st.id
JOIN game g ON g.stadium_id = st.id
LEFT JOIN ticket_seat ts ON ts.seat_id = seat.id
LEFT JOIN ticket t ON ts.ticket_id = t.id AND t.game_id = :gameId AND t.deleted_at IS NULL
WHERE g.id = :gameId
GROUP BY s.type
""", nativeQuery = true)
    List<SectionTypeSeatCountResponse> findSeatCountsByType(@Param("gameId") Long gameId);




    @Query(value = """
SELECT
    s.code AS section_code,
    COUNT(seat.id) - COUNT(t.id) AS remaining_seats
FROM seat seat
JOIN section s ON seat.section_id = s.id
JOIN stadium st ON s.stadium_id = st.id
JOIN game g ON g.stadium_id = st.id
LEFT JOIN ticket_seat ts ON ts.seat_id = seat.id
LEFT JOIN ticket t ON ts.ticket_id = t.id AND t.game_id = :gameId AND t.deleted_at IS NULL
WHERE g.id = :gameId
 AND s.type = :type
GROUP BY s.id
""", nativeQuery = true)
    List<SectionSeatCountResponse> findSeatCountsBySection(
            @Param("gameId") Long gameId,
            @Param("type") String type
    );


    @Query(value = """
    SELECT s.id AS id, s.position AS position, s.is_blind AS isBlind, CASE WHEN COUNT(t.id) > 0 THEN true ELSE false END AS isBooked
    FROM seat s
    LEFT JOIN ticket_seat ts ON s.id = ts.seat_id
    LEFT JOIN ticket t ON ts.ticket_id = t.id AND t.deleted_at IS NULL AND t.game_id = :gameId
    WHERE s.section_id = :sectionId
    GROUP BY s.id, s.position, s.is_blind
    """, nativeQuery = true)
    List<Object[]> findSeatsInfo(
            @Param("sectionId") Long sectionId,
            @Param("gameId") Long gameId
    );


    @Query("select game.id "
        + "   from Game game"
        + "  inner join Stadium stadium "
        + "          on game.stadium = stadium "
        + "  inner join Section section"
        + "          on section.stadium = stadium "
        + "  inner join Seat seat"
        + "          on seat.section = section "
        + "  where game.id = :gameId"
        + "    and seat.id in :seatIds"
        + "    and game.deletedAt is null ")
    List<Long> findValidSeatIdsByGameId(Long gameId, List<Long> seatIds);
}
