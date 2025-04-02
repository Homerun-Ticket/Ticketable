package com.example.ticketable.domain.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketable.domain.auction.entity.Auction;

public interface AuctionRepository extends JpaRepository <Auction, Long>{
}
