package com.example.ticketable.domain.auction.repository;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.ticket.entity.Ticket;

public interface AuctionTicketInfoRepositoryQuery {
	AuctionTicketInfoDto findTicketInfo(Ticket ticket);
}
