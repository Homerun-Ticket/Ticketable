package com.example.ticketable.domain.auction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketable.domain.auction.dto.AuctionTicketInfoDto;
import com.example.ticketable.domain.auction.dto.request.AuctionSearchCondition;
import com.example.ticketable.domain.auction.dto.response.AuctionResponse;
import com.example.ticketable.domain.auction.entity.Auction;
import com.example.ticketable.domain.auction.entity.AuctionTicketInfo;
import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.game.enums.GameType;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.member.role.MemberRole;
import com.example.ticketable.domain.stadium.entity.Seat;
import com.example.ticketable.domain.stadium.entity.Section;
import com.example.ticketable.domain.stadium.entity.Stadium;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.entity.TicketPayment;
import com.example.ticketable.domain.ticket.entity.TicketSeat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class AuctionRepositoryQueryImplTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private AuctionRepository auctionRepository;

	@Autowired
	private AuctionRepositoryQueryImpl auctionRepositoryQueryImpl;

	@Test
	@Transactional
	public void testFindTicketInfo() {
		// Member 생성
		Member member = Member.builder()
			.email("test@example.com")
			.password("password")
			.name("Test Member")
			.role(MemberRole.ROLE_MEMBER)
			.build();
		em.persist(member);

		// Game 생성
		Game game = Game.builder()
			.away("AwayTeam")
			.home("HomeTeam")
			.type(GameType.NORMAL)
			.point(0)
			.startTime(LocalDateTime.now())
			.build();
		em.persist(game);

		// Ticket 생성
		Ticket ticket = Ticket.builder()
			.member(member)
			.game(game)
			.build();
		em.persist(ticket);

		// TicketPayment 생성
		TicketPayment ticketPayment = TicketPayment.builder()
			.totalPoint(100_000)
			.ticket(ticket)
			.member(member)
			.build();
		em.persist(ticketPayment);

		// Stadium 생성
		Stadium stadium = Stadium.builder()
			.name("Test Stadium")
			.location("Test Location")
			.capacity(50_000)
			.build();
		em.persist(stadium);

		// Section 생성
		Section section = Section.builder()
			.type("1루")
			.code("VIP 석")
			.extraCharge(50_000)
			.stadium(stadium)
			.build();
		em.persist(section);

		// Seat 생성
		Seat seat = Seat.builder()
			.rowNum("A")
			.colNum("1")
			.isBlind(false)
			.section(section)
			.build();
		em.persist(seat);

		// TicketSeat 생성
		TicketSeat ticketSeat = TicketSeat.builder()
			.ticket(ticket)
			.seat(seat)
			.build();
		em.persist(ticketSeat);

		em.flush();
		em.clear();

		// findTicketInfo() 호출 후 DTO 검증
		AuctionTicketInfoDto dto = auctionRepository.findTicketInfo(ticket);
		assertThat(dto).isNotNull();
		assertThat(dto.getStandardPoint()).isEqualTo(100_000);
		assertThat(dto.getSectionInfo()).isEqualTo("1루|VIP 석");
		assertThat(dto.getSeatInfo()).isEqualTo("A-1");
		assertThat(dto.getSeatCount()).isEqualTo(1);
		assertThat(dto.getIsTogether()).isTrue();
	}

	@Test
	@Transactional
	public void testFindByConditions() {
		// 테스트를 위한 Member 생성 (일반 회원, 판매자, 입찰자)
		Member member = Member.builder()
			.email("buyer@example.com")
			.password("password")
			.name("Buyer")
			.role(MemberRole.ROLE_MEMBER)
			.build();
		em.persist(member);
		Member seller = Member.builder()
			.email("seller@example.com")
			.password("password")
			.name("Seller")
			.role(MemberRole.ROLE_MEMBER)
			.build();
		em.persist(seller);
		Member bidder = Member.builder()
			.email("bidder@example.com")
			.password("password")
			.name("Bidder")
			.role(MemberRole.ROLE_MEMBER)
			.build();
		em.persist(bidder);

		// Game 생성 (home: "TeamA", away: "TeamB")
		LocalDateTime gameStartTime = LocalDateTime.now().plusDays(1);
		Game game = Game.builder()
			.away("롯데")
			.home("한화")
			.type(GameType.NORMAL)
			.point(10_000)
			.startTime(gameStartTime)
			.build();
		em.persist(game);

		// Ticket 생성
		Ticket ticket = Ticket.builder()
			.member(member)
			.game(game)
			.build();
		em.persist(ticket);

		// AuctionTicketInfo 생성
		AuctionTicketInfo auctionTicketInfo = AuctionTicketInfo.builder()
			.standardPoint(100)
			.sectionInfo("VIP|A1")
			.seatInfo("A-1")
			.seatCount(1)
			.isTogether(true)
			.build();
		em.persist(auctionTicketInfo);

		// Auction 생성
		Auction auction = Auction.builder()
			.startPoint(50)
			.bidPoint(60)
			.auctionTicketInfo(auctionTicketInfo)
			.ticket(ticket)
			.seller(seller)
			.bidder(bidder)
			.build();
		em.persist(auction);

		em.flush();
		em.clear();

		// AuctionSearchCondition 생성 (home, away, startTime, seatCount, isTogether 설정)
		AuctionSearchCondition condition = new AuctionSearchCondition();
		ReflectionTestUtils.setField(condition, "home", "TeamA");
		ReflectionTestUtils.setField(condition, "away", "TeamB");
		ReflectionTestUtils.setField(condition, "seatCount", 1);
		ReflectionTestUtils.setField(condition, "isTogether", true);
		ReflectionTestUtils.setField(condition, "startTime", gameStartTime);

		PageRequest pageable = PageRequest.of(0, 10);
		PagedModel<AuctionResponse> result = auctionRepository.findByConditions(condition, pageable);
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		AuctionResponse response = result.getContent().iterator().next();
		assertThat(response.getId()).isEqualTo(auction.getId());
		assertThat(response.getStartPoint()).isEqualTo(50);
		assertThat(response.getBidPoint()).isEqualTo(60);
		assertThat(response.getStandardPoint()).isEqualTo(100);
		assertThat(response.getSectionInfo()).isEqualTo("VIP|A1");
		assertThat(response.getSeatInfo()).isEqualTo("A-1");
		assertThat(response.getSeatCount()).isEqualTo(1);
		assertThat(response.getIsTogether()).isTrue();
		assertThat(response.getHome()).isEqualTo("TeamA");
		assertThat(response.getAway()).isEqualTo("TeamB");
	}
}