package com.example.ticketable.domain.auction.entity;

import com.example.ticketable.domain.auction.enums.AuctionType;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.ticket.entity.Ticket;
import com.example.ticketable.domain.ticket.entity.TicketPayment;
import com.example.ticketable.domain.ticket.entity.TicketSeat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Auction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer startPoint;

	private Integer bidPoint;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Member seller;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bidder_id")
	private Member bidder;

	@CreatedDate
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime deletedAt;
	
	@Builder
	public Auction(Integer startPoint, Integer bidPoint, Ticket ticket, Member seller, Member bidder) {
		this.startPoint = startPoint;
		this.bidPoint = bidPoint;
		this.ticket = ticket;
		this.seller = seller;
		this.bidder = bidder;
	}

	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}
}
