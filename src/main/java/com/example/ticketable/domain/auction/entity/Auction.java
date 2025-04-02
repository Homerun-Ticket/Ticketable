package com.example.ticketable.domain.auction.entity;

import com.example.ticketable.domain.auction.enums.AuctionType;
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
public class Auction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer startPoint;
	private Integer bidPoint;
	private LocalDateTime endTime;
	
	@Enumerated(EnumType.STRING)
	private AuctionType type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Member seller;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id")
	private Member bidder;
	
	@Builder
	public Auction(Integer startPoint, Integer bidPoint, LocalDateTime endTime, AuctionType type, Ticket ticket, Member seller, Member bidder) {
		this.startPoint = startPoint;
		this.bidPoint = bidPoint;
		this.endTime = endTime;
		this.type = type;
		this.ticket = ticket;
		this.seller = seller;
		this.bidder = bidder;
	}
}
