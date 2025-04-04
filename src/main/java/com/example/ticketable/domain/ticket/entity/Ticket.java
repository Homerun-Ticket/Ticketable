package com.example.ticketable.domain.ticket.entity;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.stadium.entity.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Ticket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	private LocalDateTime deletedAt;

	@Builder
	public Ticket(Member member, Game game) {
		this.member = member;
		this.game = game;
	}
}
