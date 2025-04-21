package com.example.ticketable.domain.auction.repository;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepositoryQuery {
	Page<Auction> findByConditions(AuctionSearchCondition dto, Pageable pageable);
}
