package com.example.ticketable.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryQuery {
	boolean existsByTicket(Ticket ticket);

	Optional<Auction> findByIdAndDeletedAtIsNull(Long id);

	@EntityGraph(attributePaths = {"seller", "bidder", "ticket"})
	List<Auction> findAllByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

	List<Auction> findAllByCreatedAtBetweenAndDeletedAtIsNotNull(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

	List<Auction> findAllByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
