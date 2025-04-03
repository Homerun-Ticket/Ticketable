package com.example.ticketable.domain.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepository extends JpaRepository <Auction, Long>, AuctionRepositoryQuery{
	boolean existsByTicket(Ticket ticket);
}
