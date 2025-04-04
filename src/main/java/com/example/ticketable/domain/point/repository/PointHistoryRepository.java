package com.example.ticketable.domain.point.repository;

import com.example.ticketable.domain.point.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	@Query("select ph from PointHistory ph where ph.member.id = :memberId")
	@EntityGraph(attributePaths = "member")
	Page<PointHistory> findAllByMemberId(Long memberId, Pageable pageable);
}
