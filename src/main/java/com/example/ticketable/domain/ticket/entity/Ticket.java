package com.example.ticketable.domain.ticket.entity;

import com.example.ticketable.domain.game.entity.Game;
import com.example.ticketable.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	public void cancel() {
		deletedAt = LocalDateTime.now();
	}

	public void changeOwner(Member targetMember) {
		this.member = targetMember;
	}

	public boolean isTimeOverToAuction() {
		return this.game.getStartTime().minusHours(24).isBefore(LocalDateTime.now());
	}

	public boolean isNotOwner(Member seller) {
		return !this.member.equals(seller);
	}
}
