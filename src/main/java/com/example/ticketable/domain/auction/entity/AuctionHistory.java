package com.example.ticketable.domain.auction.entity;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.auction.enums.AuctionHistoryType;
import com.example.ticketable.domain.auction.enums.AuctionType;
import com.example.ticketable.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AuctionHistory extends Timestamped {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer point;
	
	@Enumerated(EnumType.STRING)
	private AuctionHistoryType type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id", nullable = false)
	private Member bidder;
	
	@Builder
	public AuctionHistory(Integer point, AuctionHistoryType type, Auction auction, Member bidder) {
		this.point = point;
		this.type = type;
		this.auction = auction;
		this.bidder = bidder;
	}
}
