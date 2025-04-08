package com.example.ticketable.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryQuery {
	boolean existsByTicket(Ticket ticket);

	Optional<Auction> findByIdAndDeletedAtIsNull(Long id);

	@EntityGraph(attributePaths = {"seller", "bidder", "ticket"})
	List<Auction> findAllByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

	@Query(" SELECT a "
		+ "    FROM Auction a JOIN FETCH a.ticket t JOIN FETCH a.seller s LEFT JOIN FETCH a.bidder b "
		+ "   WHERE t.game.id = :gameId")
	List<Auction> findAllByGameId(@Param("gameId") Long gameId);
}
