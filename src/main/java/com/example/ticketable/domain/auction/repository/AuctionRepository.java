package com.example.ticketable.domain.auction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepository extends JpaRepository <Auction, Long>, AuctionRepositoryQuery{
	boolean existsByTicket(Ticket ticket);
}
