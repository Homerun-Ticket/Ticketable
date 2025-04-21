package com.example.ticketable.domain.auction.service;

import static com.example.ticketable.common.exception.ErrorCode.*;
import static com.example.ticketable.domain.auction.service.AuctionService.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.auction.dto.request.AuctionBidRequest;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.auction.entity.AuctionHistory;
import com.example.ticketable.domain.auction.repository.AuctionHistoryRepository;
import com.example.ticketable.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionHistoryService {

	private final AuctionHistoryRepository auctionHistoryRepository;

	@Transactional
	public void createAuctionHistory(Auction auction, Member bidder, AuctionBidRequest dto) {
		// 6. 해당 경매기록에서, 가격이 같은 기록이 존재하면 예외처리
		if (auctionHistoryRepository.existsByAuctionAndPoint(auction, dto.getCurrentBidPoint() + BID_UNIT)) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 7. 경매기록 저장
		AuctionHistory auctionHistory = AuctionHistory.builder()
			.auction(auction)
			.bidder(bidder)
			.point(auction.getBidPoint() + BID_UNIT)
			.build();
		auctionHistoryRepository.save(auctionHistory);
	}
}
