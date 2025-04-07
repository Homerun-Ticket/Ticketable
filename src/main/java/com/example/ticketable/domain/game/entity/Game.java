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

	private String imagePath;

	private LocalDateTime startTime;

	private LocalDateTime deletedAt;
	
	@Builder
	public Game(String away, Stadium stadium, String home, GameType type, Integer point, String imagePath, LocalDateTime startTime) {
		this.stadium = stadium;
		this.away = away;
		this.home = home;
		this.type = type;
		this.point = point;
		this.imagePath = imagePath;
		this.startTime = startTime;
		this.deletedAt = null;
	}

	public void cancel() {
		deletedAt = LocalDateTime.now();
	}

	public void updateStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}
