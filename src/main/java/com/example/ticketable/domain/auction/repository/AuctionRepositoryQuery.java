package com.example.ticketable.domain.auction.repository;

import java.util.Optional;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepositoryQuery {
	AuctionTicketInfoDto findTicketInfo(Ticket ticket);
	Page<AuctionResponse> findAuctionsByConditions(AuctionSearchCondition dto, Pageable pageable);
}
