package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
