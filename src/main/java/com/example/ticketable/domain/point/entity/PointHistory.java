package com.example.ticketable.domain.point.entity;

import com.example.ticketable.common.entity.Timestamped;
import com.example.ticketable.domain.member.entity.Member;
import com.example.ticketable.domain.point.enums.PointHistoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PointHistory extends Timestamped {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer charge;
	
	@Enumerated(EnumType.STRING)
	private PointHistoryType type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@Builder
	public PointHistory(Integer charge, PointHistoryType type, Member member) {
		this.charge = charge;
		this.type = type;
		this.member = member;
	}
}
