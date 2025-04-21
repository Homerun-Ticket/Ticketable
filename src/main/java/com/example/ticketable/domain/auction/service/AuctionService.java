package com.example.ticketable.domain.auction.service;

import static com.example.ticketable.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ticketable.common.util.AuctionBidRedisUtil;
import com.example.ticketable.domain.auction.dto.event.BidUpdateEvent;
import com.example.ticketable.domain.auction.dto.request.AuctionBidRequest;
import com.example.ticketable.domain.auction.dto.response.AuctionBidResponse;
import com.example.ticketable.domain.auction.entity.AuctionTicketInfo;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	public static final int BID_UNIT = 100;
	private static final int CHUNK_SIZE = 500;

	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final AuctionTicketInfoRepository auctionTicketInfoRepository;

	private final PointService pointService;
	private final AuctionTicketInfoService auctionTicketInfoService;
	private final AuctionHistoryService auctionHistoryService;
	private final AuctionBidRedisUtil auctionBidRedisUtil;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public AuctionResponse createAuction(Auth auth, AuctionCreateRequest dto) {
		Ticket ticket = findTicket(dto);

		if (ticket.isTimeOverToAuction()) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		if (auctionRepository.existsByTicketAndDeletedAtIsNull(ticket)) {
			throw new ServerException(AUCTION_DUPLICATION);
		}

		if (auctionHistoryRepository.existsByAuction_Ticket(ticket)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		Member seller = findMember(auth);

		if (ticket.isNotOwner(seller)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		AuctionTicketInfo auctionTicketInfo = auctionTicketInfoService.createAuctionTicketInfo(ticket);

		Auction auction = Auction.builder()
			.seller(seller)
			.ticket(ticket)
			.startPoint(dto.getStartPoint())
			.bidPoint(dto.getStartPoint())
			.auctionTicketInfo(auctionTicketInfo)
			.build();

		Auction savedAuction = auctionRepository.save(auction);

		auctionBidRedisUtil.createBidKey(savedAuction);

		return AuctionResponse.of(savedAuction);
	}

	@Transactional(readOnly = true)
	public AuctionResponse getAuction(Long auctionId) {
		return AuctionResponse.of(findAuction(auctionId));
	}

	@Transactional(readOnly = true)
	public PagedModel<AuctionResponse> getAuctions(AuctionSearchCondition dto, Pageable pageable) {
		Page<Auction> pages = auctionRepository.findByConditions(dto, pageable);
		return new PagedModel<>(pages.map(AuctionResponse::of));
	}

	public AuctionBidResponse getBidPoint(Long auctionId) {
		Integer latestBidPoint = auctionBidRedisUtil.getBidPoint(auctionId);
		return new AuctionBidResponse(latestBidPoint);
	}

	@Transactional
	public AuctionResponse bidAuction(Auth auth, Long auctionId, AuctionBidRequest dto) {

		// 0. Redis 사전 검증
		auctionBidRedisUtil.validateBid(auctionId, dto.getCurrentBidPoint());

		// 1. 경매 조회
		Auction auction = findAuctionForBid(auctionId);

		// 2. 입찰자가 눈으로 확인한 금액과, 실제 입찰가가 맞지 않는 경우 예외처리
		if (auction.isBidPointChanged(dto.getCurrentBidPoint())) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 3. 경매가 종료된 경우 예외처리
		if (auction.isTimeOver()) {
			expireAuction(auction);
			throw new ServerException(AUCTION_TIME_OVER);
		}

		// 4. 경매 등록자와 입찰자가 같은 경우 예외처리
		Member bidder = findMember(auth);
		if (auction.isSameSellerAndBidder(bidder)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		// 5. 입찰가보다 낮은 금액 예외처리
		if (auction.isBidPointEnough(dto.getCurrentBidPoint())) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 6. 동일한 사람 연속입찰 예외처리
		if (auction.isSameBidder(bidder)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		// 7. 입찰자 포인트 확인 및 회수
		pointService.decreasePoint(auth.getId(), dto.getCurrentBidPoint() + BID_UNIT, PointHistoryType.BID);

		// 8~9. 해당 경매기록에서, 가격이 같은 기록이 존재하면 예외처리 + 경매기록 저장
		auctionHistoryService.createAuctionHistory(auction, bidder, dto);

		// 10. 이전 입찰자에게 입찰금 환급
		if (auction.hasBidder()) {
			pointService.increasePoint(auction.getBidder().getId(), auction.getBidPoint(), PointHistoryType.BID_REFUND);
		}

		// 11. 입찰내용 업데이트
		int nextBid = auction.getBidPoint() + BID_UNIT;
		auction.updateBid(bidder, nextBid);

		// 12. Event 발행을 통한 로직 실행순서 통제
		applicationEventPublisher.publishEvent(new BidUpdateEvent(auctionId, nextBid));

		return AuctionResponse.of(auction);
	}

	@Transactional
	public void deleteAuction(Auth auth, Long auctionId) {
		Auction auction = findAuction(auctionId);

		if (auction.hasBidder()) {
			throw new ServerException(EXIST_BID);
		}

		Member requestMember = findMember(auth);

		if (auction.isNotOwner(requestMember)) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		auctionBidRedisUtil.deleteBidKey(auctionId);

		auction.setDeletedAt();
	}

	private Auction findAuction(Long auctionId) {
		return auctionRepository.findByIdAndDeletedAtIsNullWithFetchJoin(auctionId)
			.orElseThrow(() -> new ServerException(AUCTION_NOT_FOUND));
	}

	private Auction findAuctionForBid(Long auctionId) {
		return auctionRepository.findByIdWithPessimisticLock(auctionId)
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

	private void expireAuction(Auction auction) {
		auction.setDeletedAt();

		if (auction.hasBidder()) {
			pointService.increasePoint(auction.getSeller().getId(), auction.getBidPoint(), PointHistoryType.SELL);
			auction.getTicket().changeOwner(auction.getBidder());
		}

		auctionBidRedisUtil.deleteBidKey(auction.getId());
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
			expireAuction(auction);

			// 티켓 원래 주인 경매금액 뺏기
			pointService.decreasePoint(auction.getSeller().getId(), auction.getBidPoint(), PointHistoryType.REFUND);
		}
	}

	// 경매 종료 스케쥴러
	@Scheduled(fixedRate = 60000) // 1분마다 실행
	@Transactional
	public void closeExpiredAuctions() {
		Pageable pageable = PageRequest.of(0, CHUNK_SIZE);

		LocalDateTime standardTime = LocalDateTime.now().minusHours(24);

		Page<Auction> expiredAuctions = auctionRepository.findAllByDeletedAtIsNullAndCreatedAtBetween(
			standardTime.minusMinutes(60), standardTime, pageable
		);

		if (expiredAuctions.isEmpty()) {
			return;
		}

		for (Auction auction : expiredAuctions.getContent()) {
			expireAuction(auction);
		}
	}
}
