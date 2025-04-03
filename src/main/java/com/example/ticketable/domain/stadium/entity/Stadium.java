package com.example.ticketable.domain.stadium.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Stadium {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String name;

	@Column(length = 100)
	private String location;

	private Integer capacity;

	private LocalDateTime deletedAt;
	
	@Builder
	public Stadium(String name, String location, Integer capacity) {
		this.name = name;
		this.location = location;
		this.capacity = capacity;
		this.deletedAt = null;
	}
}
