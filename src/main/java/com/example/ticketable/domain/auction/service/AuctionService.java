package com.example.ticketable.domain.auction.service;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.auction.dto.request.AuctionCreateRequest;
import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.auction.dto.response.AuctionTicketInfo;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.auction.repository.AuctionHistoryRepository;
import com.example.ticketable.domain.auction.repository.AuctionRepository;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.member.repository.MemberRepository;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;

	@Transactional
	public AuctionResponse createAuction(Auth auth, AuctionCreateRequest dto) {
		Member seller = memberRepository.findById(auth.getId())
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));

		Ticket ticket = ticketRepository.findById(dto.getTicketId())
			.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));

		if (!ticket.getMember().equals(seller)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		if (ticket.getGame().getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		if (auctionRepository.existsByTicket(ticket)) {
			throw new ServerException(AUCTION_DUPLICATION);
		}

		Auction auction = Auction.builder()
			.seller(seller)
			.ticket(ticket)
			.startPoint(dto.getStartPoint())
			.build();

		Auction savedAuction = auctionRepository.save(auction);

		AuctionTicketInfo ticketInfo = auctionRepository.findTicketInfo(ticket);

		return AuctionResponse.of(savedAuction, ticketInfo);
	}

	public AuctionResponse getAuction(Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new ServerException(AUCTION_NOT_FOUND));

		AuctionTicketInfo ticketInfo = auctionRepository.findTicketInfo(auction.getTicket());

		return AuctionResponse.of(auction, ticketInfo);
	}
}
