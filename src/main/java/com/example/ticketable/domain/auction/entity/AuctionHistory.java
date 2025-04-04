package com.example.ticketable.domain.auction.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class AuctionHistory extends Timestamped {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer point;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id", nullable = false)
	private Member bidder;

	@CreatedDate
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;
	
	@Builder
	public AuctionHistory(Integer point, Auction auction, Member bidder) {
		this.point = point;
		this.auction = auction;
		this.bidder = bidder;
	}
}
