package com.example.ticketable.domain.stadium.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20)
	private String type;

	@Column(length = 20)
	private String code;

	private Integer extraCharge;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stadium_id", nullable = false)
	private Stadium stadium;
	
	@Builder
	public Section(String type, String code, Integer extraCharge, Stadium stadium) {
		this.type = type;
		this.code = code;
		this.extraCharge = extraCharge;
		this.stadium = stadium;
	}
}
