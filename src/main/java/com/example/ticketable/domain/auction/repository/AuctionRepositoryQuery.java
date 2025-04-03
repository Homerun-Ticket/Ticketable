package com.example.ticketable.domain.auction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionTicketInfo;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionRepositoryQuery {
	AuctionTicketInfo findTicketInfo(Ticket ticket);

}
