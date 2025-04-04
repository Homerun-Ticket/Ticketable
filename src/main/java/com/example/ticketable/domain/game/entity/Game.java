package com.example.ticketable.domain.game.entity;

import com.example.ticketable.domain.game.enums.GameType;
import com.example.ticketable.domain.stadium.entity.Stadium;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Game {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String away;

	@Column(length = 50)
	private String home;

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private GameType type;

	@ManyToOne
	@JoinColumn(name = "stadium_id")
	private Stadium stadium;
	
	private Integer point;

	private LocalDateTime startTime;
	
	@Builder
	public Game(String away, String home, GameType type, Integer point, LocalDateTime startTime) {
		this.away = away;
		this.home = home;
		this.type = type;
		this.point = point;
		this.startTime = startTime;
	}
}
