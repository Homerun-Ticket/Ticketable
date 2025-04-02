package com.example.ticketable.domain.stadium.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Stadium {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String location;
	private Integer capacity;
	
	@Builder
	public Stadium(String name, String location, Integer capacity) {
		this.name = name;
		this.location = location;
		this.capacity = capacity;
	}
}
