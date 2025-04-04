package com.example.ticketable.domain.stadium.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLRestriction("deleted_at is null")
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20)
	private String position;

	private boolean isBlind;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id", nullable = false)
	private Section section;

	private LocalDateTime deletedAt;

	@Builder
	public Seat(String position, boolean isBlind, Section section) {
		this.position = position;
		this.isBlind = isBlind;
		this.section = section;
		this.deletedAt = null;
	}

	public void updateBlind() {
		this.isBlind = !isBlind;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
