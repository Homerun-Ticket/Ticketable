package com.example.ticketable.domain.auction.entity;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.ticket.entity.Ticket;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Auction extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer startPoint;

	private Integer bidPoint;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_ticket_info_id", nullable = false)
	private AuctionTicketInfo auctionTicketInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Member seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id")
	private Member bidder;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime deletedAt;

	@Builder
	public Auction(Integer startPoint, Integer bidPoint, AuctionTicketInfo auctionTicketInfo, Ticket ticket,
		Member seller, Member bidder) {
		this.startPoint = startPoint;
		this.bidPoint = bidPoint;
		this.auctionTicketInfo = auctionTicketInfo;
		this.ticket = ticket;
		this.seller = seller;
		this.bidder = bidder;
	}

	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}

	public void updateBid(Member bidder, Integer bidPoint) {
		this.bidder = bidder;
		this.bidPoint = bidPoint;
	}

	public boolean isBidPointChanged(Integer currentBidPoint) {
		return !this.bidPoint.equals(currentBidPoint);
	}

	public boolean isTimeOver() {
		return this.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now());
	}

	public boolean isSameSellerAndBidder(Member bidder) {
		return this.seller.equals(bidder);
	}

	public boolean hasBidder() {
		return this.bidder != null;
	}

	public boolean isNotOwner(Member requestMember) {
		return !this.seller.equals(requestMember);
	}
}
