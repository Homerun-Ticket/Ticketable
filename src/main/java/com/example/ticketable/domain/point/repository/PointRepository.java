package com.example.ticketable.domain.point.repository;

import com.example.ticketable.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
	
	@Query("select p from Point p join fetch p.member " +
		"where p.member.id = :memberId and p.member.deletedAt is null")
	Optional<Point> findByMemberId(@Param("memberId") Long memberId);
}
