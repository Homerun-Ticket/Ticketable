package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.TicketPayment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketPaymentRepository extends JpaRepository<TicketPayment, Long> {
	Optional<TicketPayment> findByTicketId(Long ticketId);
}
