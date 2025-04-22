package com.example.ticketable.domain.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketable.domain.auction.entity.AuctionTicketInfo;

public interface AuctionTicketInfoRepository extends JpaRepository<AuctionTicketInfo, Long>, AuctionTicketInfoRepositoryQuery {
}
