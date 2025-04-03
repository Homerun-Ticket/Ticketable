package com.example.ticketable.domain.auction.service;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.LocalDateTime;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.auction.entity.AuctionTicketInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.auction.dto.request.AuctionCreateRequest;
import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
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
			.orElseThrow(() -> new ServerException(USER_ACCESS_DENIED));

		if (!ticket.getMember().equals(seller)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		if (ticket.getGame().getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		if (auctionRepository.existsByTicket(ticket)) {
			throw new ServerException(AUCTION_DUPLICATION);
		}

		AuctionTicketInfoDto ticketInfo = auctionRepository.findTicketInfo(ticket);

		AuctionTicketInfo auctionTicketInfo = AuctionTicketInfo.builder()
			.standardPoint(ticketInfo.getStandardPoint())
			.sectionInfo(ticketInfo.getSectionInfo())
			.seatInfo(ticketInfo.getSeatInfo())
			.isTogether(ticketInfo.getIsTogether())
			.build();

		Auction auction = Auction.builder()
			.seller(seller)
			.ticket(ticket)
			.startPoint(dto.getStartPoint())
			.auctionTicketInfo(auctionTicketInfo)
			.build();

		Auction savedAuction = auctionRepository.save(auction);

		return AuctionResponse.of(savedAuction);
	}

	public AuctionResponse getAuction(Long auctionId) {
		Auction auction = auctionRepository.findAuctionById(auctionId)
			.orElseThrow(() -> new ServerException(AUCTION_NOT_FOUND));

		return AuctionResponse.of(auction);
	}

	public Page<AuctionResponse> getAuctions(AuctionSearchCondition dto, Pageable pageable) {
		return auctionRepository.findAuctionsByConditions(dto, pageable);
	}
}
