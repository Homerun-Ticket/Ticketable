package com.example.ticketable.domain.ticket.service;

import static com.example.ticketable.common.exception.ErrorCode.TICKET_NOT_FOUND;
import static com.example.ticketable.common.exception.ErrorCode.USER_ACCESS_DENIED;
import static com.example.ticketable.domain.member.role.MemberRole.ROLE_MEMBER;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.event.SeatHoldReleaseEvent;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.common.util.SeatHoldRedisUtil;
import com.example.ticketable.domain.game.service.GameCacheService;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.service.PointService;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.service.SeatService;
import com.example.ticketable.domain.ticket.dto.TicketContext;
import com.example.ticketable.domain.ticket.dto.request.TicketCreateRequest;
import com.example.ticketable.domain.ticket.dto.response.TicketResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.repository.TicketRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final TicketSeatService ticketSeatService;
	private final TicketPaymentService ticketPaymentService;
	private final PointService pointService;
	private final TicketCreateService ticketCreateService;
	private final SeatHoldRedisUtil seatHoldRedisUtil;
	private final ApplicationEventPublisher eventPublisher;
	private final GameCacheService gameCacheService;
	private final SeatService seatService;

	@Transactional(readOnly = true)
	public TicketResponse getTicket(Auth auth, Long ticketId) {
		Ticket ticket = ticketRepository.findByIdAndMemberIdWithGame(ticketId, auth.getId())
				.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));

		return convertTicketResponse(ticket);
	}

	@Transactional(readOnly = true)
	public List<TicketResponse> getAllTickets(Auth auth) {
		List<Ticket> allTickets = ticketRepository.findAllByMemberIdWithGame(auth.getId());

		return allTickets.stream().map(this::convertTicketResponse).toList();
	}

	@Transactional
	public TicketResponse reservationTicketV4(Auth auth, TicketCreateRequest ticketCreateRequest) {
		log.debug("사용자 : {}, 좌석 : {} 예매 신청", auth.getId(), ticketCreateRequest.getSeats());

		seatHoldRedisUtil.checkHeldSeatAtomic(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId(), String.valueOf(auth.getId()));
		ticketSeatService.checkDuplicateSeats(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId());

		TicketContext ticketContext = ticketCreateService.createTicketV2(auth, ticketCreateRequest);
		ticketPaymentService.paymentTicket(ticketContext);

		eventPublisher.publishEvent(new SeatHoldReleaseEvent(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId()));

		// 캐싱
		gameCacheService.handleAfterTicketChangeAll(ticketCreateRequest.getGameId(), ticketContext.getSeats().get(0));

		return ticketContext.toResponse();
	}

	@Transactional
	public void cancelTicket(Auth auth, Long ticketId) {

		// 1. 티켓 취소 처리
		Ticket ticket = ticketRepository.findByIdWithMember(ticketId)
				.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));
		if (auth.getRole() == ROLE_MEMBER && !auth.getId().equals(ticket.getMember().getId())) {
			throw new ServerException(USER_ACCESS_DENIED);
		}
		ticket.cancel();

		// 2. 환불금 조회
		int refund = ticketPaymentService.getTicketTotalPoint(ticketId);

		// 3. 사용자 포인트 환불
		pointService.increasePoint(ticket.getMember().getId(), refund, PointHistoryType.REFUND);

		// 캐싱 삭제
		gameCacheService.handleAfterTicketChangeAll(ticket.getGame().getId(), ticketSeatService.getSeat(ticketId).get(0));
	}

	/**
	 * 경기가 취소됐을때 모든 티켓을 취소 처리 해줘야 함
	 * @param gameId
	 */
	@Transactional
	public void deleteAllTicketsByCanceledGame(Long gameId) {
		ticketRepository.softDeleteAllByGameId(gameId);
	}

	/**
	 * Ticket 을 기준으로 TicketResponse에 필요한 데이터를 가져오고 매핑하는 메서드
	 *
	 * @param ticket 엔티티 객체
	 * @return TicketResponse
	 */
	private TicketResponse convertTicketResponse(Ticket ticket) {
		String title = ticket.getGame().getHome() + " vs " + ticket.getGame().getAway();
		log.debug("경기 제목 조회 title: {}", title);

		LocalDateTime startTime = ticket.getGame().getStartTime();
		log.debug("경기 시작 시간 조회 startTime : {}", startTime);

		List<String> ticketSeats = ticketSeatService.getTicketSeatsToString(ticket.getId());
		log.debug("티켓 좌석 조회 ticketSeats: {}", ticketSeats);

		int totalPoint = ticketPaymentService.getTicketTotalPoint(ticket.getId());

		log.debug("티켓 결제 금액 조회 ticketPayment: {}", totalPoint);

		return new TicketResponse(ticket.getId(), title, ticketSeats, startTime, totalPoint);
	}
}
