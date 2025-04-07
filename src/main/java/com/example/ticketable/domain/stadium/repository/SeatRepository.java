package com.example.ticketable.domain.stadium.repository;


import com.example.ticketable.domain.stadium.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SeatRepository extends JpaRepository<Seat, Long> {

    boolean existsBySectionId(Long sectionId);

}
