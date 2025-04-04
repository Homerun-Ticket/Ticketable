package com.example.ticketable.domain.ticket.service;

import static com.example.ticketable.common.exception.ErrorCode.TICKET_NOT_FOUND;
import static com.example.ticketable.common.exception.ErrorCode.USER_ACCESS_DENIED;
import static com.example.ticketable.domain.member.role.MemberRole.ROLE_MEMBER;

import com.example.ticketable.common.entity.Auth;
import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.service.GameService;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import com.example.ticketable.domain.point.service.PointService;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.service.SeatService;
import com.example.ticketable.domain.ticket.dto.request.TicketCreateRequest;
import com.example.ticketable.domain.ticket.dto.response.TicketResponse;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.repository.TicketRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final TicketSeatService ticketSeatService;
	private final TicketPaymentService ticketPaymentService;
	private final SeatService seatService;
	private final PointService pointService;
	private final GameService gameService;
	private final TicketPriceCalculator ticketPriceCalculator;

	@Transactional(readOnly = true)
	public TicketResponse getTicket(Long ticketId) {
		Ticket ticket = ticketRepository.findByIdWithGame(ticketId)
			.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));

		return convertTicketResponse(ticket);
	}

	@Transactional(readOnly = true)
	public List<TicketResponse> getAllTickets(Auth auth) {
		List<Ticket> allTickets = ticketRepository.findAllByMemberIdWithGame(auth.getId());

		return allTickets.stream().map(this::convertTicketResponse).toList();
	}

	@Transactional
	public TicketResponse createTicket(Auth auth, TicketCreateRequest ticketCreateRequest) {

		// 1. 요청 경기, 좌석 리스트 조회
		ticketSeatService.checkDuplicateSeats(ticketCreateRequest.getSeats(), ticketCreateRequest.getGameId());

		List<Seat> seats = seatService.getAllSeatEntity(ticketCreateRequest.getSeats());
		Game game = gameService.getGameEntity(ticketCreateRequest.getGameId());

		// 2. 좌석 총합 요금 계산
		int totalPoint = ticketPriceCalculator.calculateTicketPrice(game, seats);
		log.debug("좌석 금액 : {}", totalPoint);

		// 3. 포인트 차감
		pointService.decreasePoint(auth.getId(), totalPoint, PointHistoryType.RESERVATION);

		// 4. 티켓 생성
		Member member = Member.fromAuth(auth.getId());
		Ticket ticket = ticketRepository.save(new Ticket(member, game));

		// 5. 좌석 연결
		ticketSeatService.createAll(seats, game, ticket);

		// 6. 결제 기록
		ticketPaymentService.create(ticket, member, totalPoint);

		// 7. DTO 필드 세팅
		String dtoTitle = game.getHome() + " vs " + game.getAway();
		List<String> dtoSeats = seats.stream()
			.map(Seat::getPosition)
			.toList();
		LocalDateTime dtoStartTime = game.getStartTime();

		return new TicketResponse(ticket.getId(), dtoTitle, dtoSeats, dtoStartTime, totalPoint);
	}

	@Transactional
	public void deleteTicket(Auth auth, Long ticketId) {
		// 1. 티켓 취소 처리
		Ticket ticket = ticketRepository.findByIdWithMember(ticketId)
			.orElseThrow(() -> new ServerException(TICKET_NOT_FOUND));
		if (auth.getRole() == ROLE_MEMBER && !auth.getId().equals(ticket.getMember().getId())) {
			throw new ServerException(USER_ACCESS_DENIED);
		}
		ticket.delete();

		// 2. 환불금 조회
		int refund = ticketPaymentService.getTicketTotalPoint(ticketId);

		// 3. 사용자 포인트 환불
		pointService.increasePoint(ticket.getMember().getId(), refund, PointHistoryType.REFUND);
	}

	/**
	 * 경기가 취소됐을때 모든 티켓을 취소 처리 해줘야 함
	 * @param gameId
	 */
	@Transactional
	public void deleteAllTicketsByCanceledGame(Auth auth, Long gameId) {
		List<Ticket> allTicketsByGameId = ticketRepository.findAllByGameId(gameId);
		allTicketsByGameId.forEach(ticket -> deleteTicket(auth, ticket.getId()));
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
