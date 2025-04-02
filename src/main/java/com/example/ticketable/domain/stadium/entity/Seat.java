package com.example.ticketable.domain.stadium.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20)
	private String row;

	@Column(length = 20)
	private String col;

	private boolean isBlind;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id", nullable = false)
	private Section section;

	@Builder
	public Seat(String row, String col, boolean isBlind, Section section) {
		this.row = row;
		this.col = col;
		this.isBlind = isBlind;
		this.section = section;
	}
}
