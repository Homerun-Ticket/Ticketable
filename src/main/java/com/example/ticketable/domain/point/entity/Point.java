package com.example.ticketable.domain.point.entity;

import com.example.ticketable.common.exception.ServerException;
import com.example.ticketable.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.ticketable.common.exception.ErrorCode.NOT_ENOUGH_POINT;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Point {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer point;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@Builder
	public Point(Integer point, Member member) {
		this.point = point;
		this.member = member;
	}
	
	public void plusPoint(Integer charge) {
		this.point += charge;
	}
	
	public void minusPoint(Integer charge) {
		if (this.point < charge) {
			throw new ServerException(NOT_ENOUGH_POINT);
		}
		this.point -= charge;
	}
}
