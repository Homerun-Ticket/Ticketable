package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SeatRepository extends JpaRepository<Seat, Long> {

    boolean existsBySectionId(Long sectionId);

    @Query("select seat "
        + "   from Seat seat "
        + "   join fetch seat.section "
        + "   join fetch seat.section.stadium "
        + "  where seat.id in :ids ")
    List<Seat> findAllByIds(List<Long> ids);
}
