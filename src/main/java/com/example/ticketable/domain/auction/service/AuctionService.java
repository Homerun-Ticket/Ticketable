package com.example.ticketable.domain.auction.service;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.auction.dto.request.AuctionBidRequest;
import com.example.ticketable.domain.auction.entity.AuctionHistory;
import com.example.ticketable.domain.auction.entity.AuctionTicketInfo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.example.ticketable.domain.auction.repository.AuctionTicketInfoRepository;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.member.repository.MemberRepository;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.service.PointService;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private static final int BID_UNIT = 100;

	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final AuctionTicketInfoRepository auctionTicketInfoRepository;
	private final PointService pointService;

	@Transactional
	public AuctionResponse createAuction(Auth auth, AuctionCreateRequest dto) {
		Ticket ticket = findTicket(dto);

		if (ticket.getGame().getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		Member seller = findMember(auth);

		if (!ticket.getMember().equals(seller)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		if (auctionRepository.existsByTicket(ticket)) {
			throw new ServerException(AUCTION_DUPLICATION);
		}

		AuctionTicketInfoDto ticketInfo = auctionRepository.findTicketInfo(ticket);

		AuctionTicketInfo auctionTicketInfo = AuctionTicketInfo.builder()
			.standardPoint(ticketInfo.getStandardPoint())
			.sectionInfo(ticketInfo.getSectionInfo())
			.seatInfo(ticketInfo.getSeatInfo())
			.seatCount(ticketInfo.getSeatCount())
			.isTogether(ticketInfo.getIsTogether())
			.build();

		AuctionTicketInfo savedAuctionTicketInfo = auctionTicketInfoRepository.save(auctionTicketInfo);

		Auction auction = Auction.builder()
			.seller(seller)
			.ticket(ticket)
			.startPoint(dto.getStartPoint())
			.auctionTicketInfo(savedAuctionTicketInfo)
			.build();

		Auction savedAuction = auctionRepository.save(auction);

		return AuctionResponse.of(savedAuction);
	}

	@Transactional(readOnly = true)
	public AuctionResponse getAuction(Long auctionId) {
		return AuctionResponse.of(findAuction(auctionId));
	}

	@Transactional(readOnly = true)
	public PagedModel<AuctionResponse> getAuctions(AuctionSearchCondition dto, Pageable pageable) {
		return auctionRepository.findByConditions(dto, pageable);
	}

	@Transactional
	public AuctionResponse bidAuction(Auth auth, Long auctionId, AuctionBidRequest dto) {

		pointService.decreasePoint(auth.getId(), dto.getBidPoint(), PointHistoryType.BID);

		Auction auction = findAuction(auctionId);

		if (auction.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		Member bidder = findMember(auth);

		if (auction.getSeller().equals(bidder)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		AuctionHistory auctionHistory = AuctionHistory.builder()
			.auction(auction)
			.bidder(bidder)
			.point(dto.getBidPoint())
			.build();

		auctionHistoryRepository.save(auctionHistory);

		if (auction.getBidder() != null) {
			pointService.increasePoint(auction.getBidder().getId(), auction.getBidPoint(), PointHistoryType.BID_REFUND);
		}

		auction.updateBid(bidder, dto.getBidPoint() + BID_UNIT);

		return AuctionResponse.of(auction);
	}

	@Transactional
	public void deleteAuction(Auth auth, Long auctionId) {
		Auction auction = findAuction(auctionId);

		if (auction.getBidder() != null) {
			throw new ServerException(EXIST_BID);
		}

		Member requestMember = findMember(auth);

		if (!auction.getSeller().equals(requestMember)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		auction.setDeletedAt();
	}

	private Auction findAuction(Long auctionId) {
		return auctionRepository.findByIdAndDeletedAtIsNull(auctionId)
			.orElseThrow(() -> new ServerException(AUCTION_NOT_FOUND));
	}

	private Ticket findTicket(AuctionCreateRequest dto) {
		return ticketRepository.findByIdWithGameAndMember(dto.getTicketId())
			.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));
	}

	private Member findMember(Auth auth) {
		return memberRepository.findById(auth.getId())
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
	}

	/*
	 * 경기 취소 시 로직
	 * 최종 낙찰자에 대한 포인트 환불 + 판매자 포인트 회수
	 */
	@Transactional
	public void deleteAllAuctionsByCanceledGame(Long gameId) {
		List<Auction> auctions = auctionRepository.findAllByGameId(gameId);

		if (auctions.isEmpty()) {
			return;
		}

		for (Auction auction : auctions) {
			// 경매 중인 경우, 경매 강제 종료 처리 (소프트딜리트)
			if (auction.getDeletedAt() == null){
				auction.setDeletedAt();
			}

			// 낙찰자 혹은 현재 최고 입찰자 환불
			if (auction.getBidder() != null) {
				pointService.increasePoint(auction.getBidder().getId(), auction.getBidPoint(), PointHistoryType.REFUND);
			}

			// 티켓 원래 주인 경매금액 뺏기
			pointService.decreasePoint(auction.getSeller().getId(), auction.getBidPoint(), PointHistoryType.REFUND);
		}
	}

	@Scheduled(fixedRate = 60000) // 1분마다 실행
	@Transactional
	public void closeExpiredAuctions() {
		LocalDateTime standardTime = LocalDateTime.now().minusHours(24);

		List<Auction> expiredAuctions = auctionRepository.findAllByCreatedAtBetweenAndDeletedAtIsNull(
			standardTime.minusMinutes(60), standardTime
		);

		if (expiredAuctions.isEmpty()) {
			return;
		}

		for (Auction auction : expiredAuctions) {
			auction.setDeletedAt();

			if (auction.getBidder() != null) {
				pointService.increasePoint(auction.getSeller().getId(), auction.getBidPoint(), PointHistoryType.SELL);
				auction.getTicket().changeOwner(auction.getBidder());
			}
		}
	}
}
