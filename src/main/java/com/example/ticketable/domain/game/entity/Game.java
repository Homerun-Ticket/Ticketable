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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stadium_id", nullable = false)
	private Stadium stadium;

	@Column(length = 50, nullable = false)
	private String away;

	@Column(length = 50, nullable = false)
	private String home;

	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private GameType type;

	@Column(nullable = false)
	private Integer point;

	@Column(nullable = false)
	private String imagePath;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime ticketingStartTime;

	private LocalDateTime deletedAt;

	@Builder
	public Game(String away, Stadium stadium, String home, GameType type, Integer point, String imagePath, LocalDateTime startTime, LocalDateTime ticketingStartTime) {
		this.stadium = stadium;
		this.away = away;
		this.home = home;
		this.type = type;
		this.point = point;
		this.imagePath = imagePath;
		this.startTime = startTime;
		this.ticketingStartTime = ticketingStartTime;
		this.deletedAt = null;
	}

	public void cancel() {
		deletedAt = LocalDateTime.now();
	}

	public void updateStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}
