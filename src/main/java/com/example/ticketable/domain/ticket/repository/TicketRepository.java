package com.example.ticketable.domain.ticket.repository;

import com.example.ticketable.domain.ticket.entity.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.game "
		+ "  WHERE t.id = :id "
		+ "    AND t.deletedAt is null ")
	Optional<Ticket> findByIdWithGame(Long id);

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.member "
		+ "  WHERE t.id = :id "
		+ "    AND t.deletedAt is null ")
	Optional<Ticket> findByIdWithMember(Long id);

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.game "
		+ "  WHERE t.member.id = :memberId "
		+ "    AND t.deletedAt is null ")
	List<Ticket> findAllByMemberIdWithGame(Long memberId);


	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.game "
		+ "  WHERE t.game.id = :gameId "
		+ "    AND t.deletedAt is null ")
	List<Ticket> findAllByGameId(Long gameId);

	@Query("SELECT t "
		+ "   FROM Ticket t JOIN FETCH t.member JOIN FETCH t.game "
		+ "  WHERE t.id = :id "
		+ "    AND t.deletedAt is null ")
	Optional<Ticket>findByIdWithGameAndMember(Long id);

	@Modifying
	@Query("update Ticket t "
		+ "    set t.deletedAt = now()"
		+ " where t.game.id = :gameId ")
	void softDeleteAllByGameId(Long gameId);
}
