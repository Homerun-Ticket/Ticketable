package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.TicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketPaymentRepository extends JpaRepository<TicketPayment, Long> {

}
