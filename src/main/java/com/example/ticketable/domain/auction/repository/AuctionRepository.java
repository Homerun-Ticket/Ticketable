package com.example.ticketable.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

import jakarta.persistence.LockModeType;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryQuery {
	@Query(" SELECT a " +
		"      FROM Auction a JOIN FETCH a.ticket t JOIN FETCH t.game g JOIN FETCH a.auctionTicketInfo ati " +
		"     WHERE a.id = :id AND a.deletedAt IS NULL")
	Optional<Auction> findByIdAndDeletedAtIsNullWithFetchJoin(@Param("id") Long id);

	@Query(" SELECT a " +
		"      FROM Auction a JOIN FETCH a.ticket t JOIN FETCH a.seller s JOIN FETCH a.bidder b " +
		"     WHERE t.game.id = :gameId")
	List<Auction> findAllByGameId(@Param("gameId") Long gameId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(" SELECT a " +
		"      FROM Auction a " +
		"     WHERE a.id = :id AND a.deletedAt is null")
	Optional<Auction> findByIdWithPessimisticLock(@Param("id") Long id);

	boolean existsByTicketAndDeletedAtIsNull(Ticket ticket);

	@EntityGraph(attributePaths = {"seller", "bidder", "ticket"})
	Page<Auction> findAllByDeletedAtIsNullAndCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, Pageable pageable);
}
