package com.example.ticketable.domain.ticket.entity;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class TicketPayment extends Timestamped {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer totalPoint;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public TicketPayment(Integer totalPoint, Ticket ticket, Member member) {
		this.totalPoint = totalPoint;
		this.ticket = ticket;
		this.member = member;
	}
	
}
