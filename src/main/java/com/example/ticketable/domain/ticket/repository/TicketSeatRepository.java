package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.entity.TicketSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketSeatRepository extends JpaRepository<TicketSeat, Long> {

	@Query("select ts "
		+ "   from TicketSeat ts join fetch ts.seat "
		+ "  where ts.ticket.id = :ticketId ")
	List<TicketSeat> findByTicketIdWithSeat(Long ticketId);
}
