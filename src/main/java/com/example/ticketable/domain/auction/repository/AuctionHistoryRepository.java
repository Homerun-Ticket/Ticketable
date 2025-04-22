package com.example.ticketable.domain.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.auction.entity.AuctionHistory;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionHistoryRepository extends JpaRepository <AuctionHistory, Long> {
	boolean existsByAuctionAndPoint(Auction auction, Integer point);
	boolean existsByAuction_Ticket(Ticket ticket);
}
