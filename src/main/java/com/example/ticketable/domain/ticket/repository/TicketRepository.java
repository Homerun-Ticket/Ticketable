package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.game "
		+ "  WHERE t.id = :id ")
	Optional<Ticket> findByIdWithGame(Long id);

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.game "
		+ "  WHERE t.member.id = :memberId ")
	List<Ticket> findAllByMemberIdWithGame(Long memberId);
}
