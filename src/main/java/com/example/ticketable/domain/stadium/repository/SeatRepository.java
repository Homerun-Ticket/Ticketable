package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s " +
            "LEFT JOIN TicketSeat  ts ON s.id = ts.seat.id " +
            "LEFT JOIN Ticket t ON ts.ticket.id = t.id AND t.deletedAt IS NOT NULL " +
            "WHERE s.section.id = :sectionId " +
            "AND ts IS NULL"
            )
    List<Seat> findUnbookSeatsBySectionId(Long sectionId);

    List<Seat> findBySectionId(Long sectionId);

    boolean existsBySectionId(Long sectionId);

    @Query("select seat "
        + "   from Seat seat "
        + "   join fetch seat.section "
        + "   join fetch seat.section.stadium "
        + "  where seat.id in :ids ")
    List<Seat> findAllByIds(List<Long> ids);
}
