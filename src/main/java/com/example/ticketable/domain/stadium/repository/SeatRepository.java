package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s " +
            "LEFT JOIN TicketSeat  ts ON s.id = ts.seat.id " +
            "LEFT JOIN Ticket t ON ts.ticket.id = t.id " +
            "WHERE s.section = :sectionId " +
            "AND (t.deletedAt IS NOT NULL " +
            "OR ts IS NULL)")
    List<Seat> findUnbookSeatsBySectionId(Long sectionId);

    List<Seat> findBySectionId(Long sectionId);
}
