package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.TicketSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSeatRepository extends JpaRepository<TicketSeat, Long> {

}
